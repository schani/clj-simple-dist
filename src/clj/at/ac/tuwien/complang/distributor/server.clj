(ns at.ac.tuwien.complang.distributor.server)

(defn create-or-get-registry [port]
  (try
   (java.rmi.registry.LocateRegistry/createRegistry port)
   (catch java.rmi.server.ExportException _
     (java.rmi.registry.LocateRegistry/getRegistry port))))

(defn register-server [registry server]
  (.rebind registry
	   "clj-simple-dist-worker"
	   (java.rmi.server.UnicastRemoteObject/exportObject server 9000)))
