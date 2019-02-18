(ns ethlint-pre-commit.core
  (:require ["ethlint/lib/cli" :as cli]
            [clojure.tools.cli :refer [parse-opts]]
            ))

(set! *warn-on-infer* true)


(defn exit
  "Exit with the given status."
  [status]
  (.exit js/process status))


(defn run-solium
  [{:keys [config]} args]
  (when-not (empty? args)
    (let [file-args (interleave (repeat "-f")
                                args)
          cmd (apply vector "node" "ethlint"
                     "--no-soliumignore"
                     "--config" config
                     file-args)]
      (cli/execute (clj->js cmd)))))


(defn show-usage
  [summary]
  (println (str
            "Usage: ethlint-pre-commit [options] SOLIDITY_FILE ..." \newline
            "Options:" \newline
            summary)))


(def cli-options
  [["-c" "--config CONFIG" "path to .soliumrc file" :default ".soliumrc"]
   ["-h" "--help" "show help"]])


(defn -main
  [& args]
  (let [{:keys [options arguments summary errors]} (parse-opts args cli-options)]
    (try
      (cond
        (:help options)
          (show-usage summary)
        :else
          (run-solium options arguments))
      (exit 0)
      (finally
        (exit 1)))))

(set! *main-cli-fn* -main)
