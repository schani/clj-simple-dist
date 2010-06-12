(ns at.ac.tuwien.complang.distributor.vsc
  (:use clojure.contrib.def
	at.ac.tuwien.complang.distributor.client
	at.ac.tuwien.complang.distributor.convenience)
  (:import [java.rmi.server RMISocketFactory]))

(defvar- is-worker (System/getProperty "at.ac.tuwien.complang.distributor.worker"))
(defvar- server (or (System/getProperty "at.ac.tuwien.complang.distributor.vsc-server") "localhost"))

(defvar- connection (connect "localhost" 1099))

(defvar- functions (atom {}))

(defn vsc-add-fn [name version func]
  (swap! functions assoc {:name name :version version} func))

(defn vsc-register-worker []
  (println (str @functions))
  (register-worker server (str (.getHostName (java.net.InetAddress/getLocalHost)) "-" (System/getProperty "pid"))
		   @functions))

(defmacro vsc-fn [name version args & body]
  (let [name-string (str name)
	vsc-fn-name (symbol (str name-string "-vsc"))
	fn-version-name (symbol (str name-string "-vsc-version"))
	conn connection]
    `(do
       (defn ~name ~args
	 ~@body)
       (def ~vsc-fn-name (worker-function ~conn {:name ~name-string :version ~version} ~name))
       (vsc-add-fn ~name-string ~version ~name))))

(defvar- host-map
  {"vsc.tuwien.ac.at" "localhost"
   "172.16.21.56" "localhost"
   "128.130.35.5" "localhost"})

(defvar- vsc-socket-factory
  (let [factory (RMISocketFactory/getDefaultSocketFactory)]
    (proxy [RMISocketFactory] []
      (createServerSocket [port]
			  (.createServerSocket factory port))
      (createSocket [host port]
		    (.createSocket factory (or (host-map host) host) port)))))

(if is-worker
  (println "I'm a worker")
  (RMISocketFactory/setSocketFactory vsc-socket-factory))
