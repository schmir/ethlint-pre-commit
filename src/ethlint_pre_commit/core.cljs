(ns ethlint-pre-commit.core
  (:require ["ethlint/lib/cli" :as cli]))

(set! *warn-on-infer* true)

(defn exit
  "Exit with the given status."
  [status]
  (.exit js/process status))

(defn -main
  [& args]

  (let [file-args (interleave (repeat "-f")
                              args)
        cmd (apply vector "ethlint" file-args)]
    (try
      (when-not (empty? file-args)
        (cli/execute (clj->js cmd)))
      (exit 0)
      (finally
        (exit 1)))))

(set! *main-cli-fn* -main)
