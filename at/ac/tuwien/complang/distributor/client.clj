(ns at.ac.tuwien.complang.distributor.client)

(defn connect [server port]
  (let [registry (java.rmi.registry.LocateRegistry/getRegistry server port)]
    (.lookup registry "Server")))

(defn worker-function [distributor name local-fun]
  (fn [& args]
    (.compute distributor name args)))
