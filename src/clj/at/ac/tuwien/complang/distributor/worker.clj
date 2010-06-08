(ns at.ac.tuwien.complang.distributor.worker
  (:import [at.ac.tuwien.complang.distributor Worker NotImplementedException]))

(defn worker-server [funcs]
  (reify
   Worker
   (compute [this fun-name args]
	    (let [fun (funcs fun-name)]
	      (if fun
		(apply fun args)
		(throw (NotImplementedException.)))))))
