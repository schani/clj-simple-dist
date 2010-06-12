(ns at.ac.tuwien.complang.distributor.vsc
  (:use clojure.contrib.def
	at.ac.tuwien.complang.distributor.client)
  (:import [java.rmi.server RMISocketFactory]))

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

(defvar- host-map
  {"vsc.tuwien.ac.at" "localhost"
   "172.16.21.56" "localhost"
   "128.130.35.5" "localhost"})

(defvar- vsc-socket-factory
  (let [factory (RMISocketFactory/getDefaultSocketFactory)]
    (proxy [RMISocketFactory] []
      (createServerSocket [port]
			  (.createServerSocket factory))
      (createSocket [host port]
		    (.createSocket factory (or (host-map host) host) port)))))

(RMISocketFactory/setSocketFactory vsc-socket-factory)
