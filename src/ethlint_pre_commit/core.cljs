(ns ethlint-pre-commit.core
  (:require ["ethlint/lib/reporters/gcc" :as gcc]
            ["ethlint/lib/solium" :as solium]
            [cljs-node-io.core :as io :refer [slurp]]
            [clojure.tools.cli :refer [parse-opts]]
            [cljs.nodejs :as nodejs]))

(set! *warn-on-infer* true)
(nodejs/enable-util-print!)


(defn exit
  "Exit with the given status."
  [status]
  (.exit js/process status))


(defn read-soliumrc
  [filename]
  (-> filename
      slurp
      js/JSON.parse))


(defn report-errors
  [filename source errors]
  (gcc/report filename source (clj->js errors) nil))


(defn lint-source-code
  [user-config source-code]
  (try
    (js->clj (solium/lint source-code user-config)
             :keywordize-keys true)
    (catch js/Error err
      (if (= "SyntaxError" (.-name err) )
        [{:ruleName ""
          :type "error"
          :message (str "Syntax error: unexpected token '" (.-found err)"'" )
          :line (.. err -location -start -line)
          :column (.. err -location -start -column)}]
        (throw err)))))


(defn lint-file
  [user-config filename]
  (let [source (slurp filename)
        errors (lint-source-code user-config source)]
    (report-errors filename source errors)
    {:filename filename
     :counts-by-type (frequencies (map :type errors))}))


(defn run-solium
  [{:keys [config]} args]

  (let [user-config (read-soliumrc config)
        lint-results (mapv (partial lint-file user-config) args)
        sum-counts-by-type (apply merge-with +
                                  {"warning" 0, "error" 0}
                                  (map :counts-by-type lint-results))
        num-errors (sum-counts-by-type "error")
        num-warnings (sum-counts-by-type "warning")]
    #_(println (str num-errors " Errors, " num-warnings " Warnings in " (count args) " files"))
    num-errors))


(defn show-usage
  [summary]
  (println (str
            "Usage: ethlint-pre-commit [options] SOLIDITY_FILE ..." \newline
            "Options:" \newline
            summary)))


(def cli-options
  [["-c" "--config CONFIG" "path to .soliumrc.json file" :default ".soliumrc.json"]
   ["-h" "--help" "show help"]])


(defn -main
  [& args]
  (let [{:keys [options arguments summary errors]} (parse-opts args cli-options)]
    (try
      (cond
        (:help options)
          (do
            (show-usage summary)
            (exit 0))
        :else
          (exit (if (zero? (run-solium options arguments))
                  0
                  1)))
      (catch :default err
        (println "internal error" err))
      (finally
        (exit 1)))))

(set! *main-cli-fn* -main)
