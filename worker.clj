(add-classpath "file:///Users/schani/Work/clojure/dist/")

(def *rmi-registry* (java.rmi.registry.LocateRegistry/createRegistry 1099))

(defn stop-rmi []
  (java.rmi.server.UnicastRemoteObject/unexportObject *rmi-registry* true))

(defn worker-server [funcs]
  (proxy [at.ac.tuwien.complang.distributor.DistributionServer] []
    (compute [fun-name args]
	     (let [fun (funcs fun-name)]
	       (if fun
		 (apply fun args)
		 (throw (Exception. "no such function")))))))

(defn register-server [server]
  (.bind *rmi-registry*
	 "Server"
	 (java.rmi.server.UnicastRemoteObject/exportObject server 0)))

(defn fib [n]
  (if (< n 2)
    n
    (+ (fib (- n 1)) (fib (- n 2)))))

(def *server* (worker-server {"fib" fib "args" (fn [& args] (str args))}))
(register-server *server*)
