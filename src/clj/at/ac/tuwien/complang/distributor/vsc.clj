(ns at.ac.tuwien.complang.distributor.vsc
  (:use clojure.contrib.def
	at.ac.tuwien.complang.distributor.client))

(defvar- connection (connect "localhost" 1099))

(defmacro vsc-fn [name version args & body]
  (let [name-string (str name)
	vsc-fn-name (symbol (str name-string "-vsc"))
	fn-version-name (symbol (str name-string "-vsc-version"))
	conn connection]
    `(do
       (defn ~name ~args
	 ~@body)
       (def ~fn-version-name ~version)
       (def ~vsc-fn-name (worker-function ~conn {:name ~name-string :version ~version} ~name)))))
