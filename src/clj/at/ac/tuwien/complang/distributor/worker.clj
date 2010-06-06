(ns at.ac.tuwien.complang.distributor.worker
  (:import [at.ac.tuwien.complang.distributor DistributionServer NotImplementedException]))

(defn worker-server [funcs]
  (reify
   DistributionServer
   (compute [this fun-name args]
	    (let [fun (funcs fun-name)]
	      (if fun
		(apply fun args)
		(throw (NotImplementedException.)))))))
