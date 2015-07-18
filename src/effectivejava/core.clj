(ns effectivejava.core
  (:require [clojure.tools.cli :refer [parse-opts]])
  (:use [effectivejava.linter])
  (:use [effectivejava.interactive])
  (:use [effectivejava.cli])
  (:gen-class :main true))

(def self-exclusive-modes-error
  "Linter, interactive and query mode are self exclusive")

(defn usageError [banner opts msg]
  (if (clojure.string/blank? msg)
    (println "Incorrect usage")
    (println (str "Incorrect usage: " msg)))
  (when (:errors opts)
    (doseq [e (:errors opts)]
      (println " * " e)))
  (println banner))

(defn info [msg]
  (println " [info] " msg))

(defn conflicting-options?
  "Return true if opts contains self-exclusive modes"
  [opts]
  (let [self-exclusive-modes [:linter :interactive :query]]
    (->> (select-keys opts self-exclusive-modes)
         (vals)
         (filter identity)
         (count)
         (< 1))))


;; The following methods have been extracted from the main function.
;; These methods should probably be moved to a different namespace.

(defn exit-error!
  "Added because you cannot mock System/exit"
  []
  (System/exit 1))

(defn exit-success!
  "Added because you cannot mock System/exit"
  []
  (System/exit 0))

(defn treat-possible-errors [opts banner]
  (when (:errors opts)
    (usageError banner opts "")
    (exit-error!))
  (when (conflicting-options? opts)
    (usageError banner opts self-exclusive-modes-error)
    (exit-error!)))

(defn run-linter-mode [opts]
  (when-not (:dir opts)
    (info "Linter, no directory indicated. Using current directory")
    (linter ".")
    (exit-success!))
  (linter (:dir opts))
  (exit-success!))

(defn run-interactive-mode []
  (interactive {})
  (exit-success!))

(defn show-help [banner]
  (println "Printing help message, as asked")
  (println banner)
  (exit-success!))

(defn run-query-mode [opts banner]
  (if
   (and
    (:dir opts)
    (:query opts)
    ((keyword (:query opts)) operations)
    (nil? (:errors opts)))
    (run opts)
    (do (usageError banner opts "")
        (exit-error!))))

(defn -main
  [& args]
  (let [optsMap (parse-opts args cliOpts)
        opts (:options optsMap)
        banner (:summary optsMap)]
    (treat-possible-errors opts banner)
    (cond
      (:linter opts) (run-linter-mode opts)
      (:interactive opts) (run-interactive-mode)
      (:help opts) (show-help banner)
      :else (run-query-mode opts banner))))
