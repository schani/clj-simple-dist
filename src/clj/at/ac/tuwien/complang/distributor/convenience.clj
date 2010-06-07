(ns at.ac.tuwien.complang.distributor.convenience
  (:use clojure.contrib.def
	at.ac.tuwien.complang.distributor.server
	at.ac.tuwien.complang.distributor.worker
	at.ac.tuwien.complang.distributor.distributor))

(defvar- *worker-port* 1098)
(defvar- *distributor-port* 1099)

(defn- make-registry-func [port]
  (let [registry (atom nil)]
    (fn []
      (swap! registry (fn [r]
			(if r
			  r
			  (create-or-get-registry port)))))))

(defvar- worker-registry (make-registry-func *worker-port*))

(def register-worker
     (let [server (atom nil)]
       (fn [funcs]
	 (let [s (worker-server funcs)]
	   (register-server (worker-registry) s)
	   (reset! server s)))))

(defvar- distributor-registry (make-registry-func *distributor-port*))

(def register-distributor
     (let [server (atom nil)]
       (fn [workers]
	 (let [s (distributor-server (map (fn [w] {:host w :port *worker-port*}) workers))]
	   (register-server (distributor-registry) s)
	   (reset! server s)))))
