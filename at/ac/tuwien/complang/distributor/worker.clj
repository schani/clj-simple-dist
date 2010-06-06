(ns at.ac.tuwien.complang.distributor.worker
  (:import [at.ac.tuwien.complang.distributor DistributionServer]))

(defn worker-server [funcs]
  (proxy [at.ac.tuwien.complang.distributor.DistributionServer] []
    (compute [fun-name args]
	     (let [fun (funcs fun-name)]
	       (if fun
		 (apply fun args)
		 (throw (Exception. "no such function")))))))
