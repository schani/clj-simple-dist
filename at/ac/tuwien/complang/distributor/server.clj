(ns at.ac.tuwien.complang.distributor.server)

(defn create-registry [port]
  (java.rmi.registry.LocateRegistry/createRegistry port))

(defn register-server [registry server]
  (.rebind registry
	   "Server"
	   (java.rmi.server.UnicastRemoteObject/exportObject server 0)))
