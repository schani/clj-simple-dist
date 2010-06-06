(ns at.ac.tuwien.complang.distributor.client)

(defn- connect-thread-func [host port server-atom]
  (loop []
    (if @server-atom
      (do
	(Thread/sleep 1000)
	(recur))
      (do
	(try
	 (let [registry (java.rmi.registry.LocateRegistry/getRegistry host port)
	       server (.lookup registry "Server")]
	   (reset! server-atom server))
	 (catch java.rmi.RemoteException _
	   (Thread/sleep 1000))
	 (catch java.rmi.NotBoundException _
	   (Thread/sleep 1000)))
	(recur)))))

(defn connect [host port]
  (let [server-atom (atom nil)
	thread (Thread. (fn [] (connect-thread-func host port server-atom)))]
    (.setDaemon thread true)
    (.start thread)
    server-atom))

(defn worker-function [server-atom name local-fun]
  (fn [& args]
    (loop []
      (let [server @server-atom]
	(if server
	  (let [result (try
			(.compute server name args)
			(catch java.rmi.ConnectException _
			  (reset! server-atom nil)
			  ::failure))]
	    (if (= result ::failure)
	      (recur)
	      result))
	  (apply local-fun args))))))
