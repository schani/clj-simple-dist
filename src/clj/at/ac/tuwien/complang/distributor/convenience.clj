(ns at.ac.tuwien.complang.distributor.convenience
  (:use clojure.contrib.def
	at.ac.tuwien.complang.distributor.server
	at.ac.tuwien.complang.distributor.worker
	at.ac.tuwien.complang.distributor.distributor))

(defvar- *port* 1099)

(defvar- registry
  (let [reg-atom (atom nil)]
    (fn []
      (swap! reg-atom, (fn [r]
			 (if r
			   r
			   (create-or-get-registry *port*)))))))

;; we need to keep references to the servers in an atom because the
;; registry apparently only keeps weak references to its registered
;; objects.

(def register-distributor
     (let [server (atom nil)]
       (fn []
	 (let [s (distributor-server)]
	   (register-server (registry) s)
	   (reset! server s)))))

(def register-worker
  (let [server (atom nil)]
    (fn [host name funcs]
      (let [reg (java.rmi.registry.LocateRegistry/getRegistry host *port*)
	    distributor (.lookup reg "clj-simple-dist-worker")
	    s (worker-server funcs)]
	(.register distributor (java.rmi.server.UnicastRemoteObject/exportObject s 0) name)
	(reset! server s)))))
