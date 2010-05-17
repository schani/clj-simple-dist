(add-classpath "file:///Users/schani/Work/clojure/dist/")

(def *rmi-registry* (java.rmi.registry.LocateRegistry/createRegistry 1099))

(defn stop-rmi []
  (java.rmi.server.UnicastRemoteObject/unexportObject *rmi-registry* true))

(defn worker-server [funcs]
  (let [computations (ref {})
	id-for-job (fn [job]
		     (loop [hash (.hashCode job)]
		       (let [id (str hash)]
			 (if (contains? @computations id)
			   (recur (inc hash))
			   id))))]
    (proxy [at.ac.tuwien.complang.distributer.DistributionServer] []
      (compute [fun args]
	       (if (contains? funcs fun)
		 (let [job {:fun (funcs fun) :args args}]
		   (dosync
		    (let [id (id-for-job job)]
		      (alter computations assoc id job)
		      id)))
		 (throw (Exception. "no such function"))))
      (ask [id]
	   (let [job (@computations id)]
	     (if job
	       (let [result (apply (:fun job) (:args job))]
		 (dosync
		  (alter computations dissoc id))
		 result)
	       (throw (Exception. "job not found"))))))))

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
