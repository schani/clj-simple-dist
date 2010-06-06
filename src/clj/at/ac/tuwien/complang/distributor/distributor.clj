(ns at.ac.tuwien.complang.distributor.distributor
  (:import [at.ac.tuwien.complang.distributor DistributionServer NotImplementedException])
  (:use at.ac.tuwien.complang.distributor.client))

;; workers is a seq of maps of the form {:host <host> :port <port>}
(defn distributor-server [workers]
  (let [connected-workers (into {} (map (fn [worker] [worker (connect (:host worker) (:port worker))]) workers))
	worker-loads (agent (into {} (map (fn [worker] [worker 0]) workers)))]
    (reify
     DistributionServer
     (compute [this fun args]
	      (let [loads @worker-loads
		    sorted (sort-by #(loads %) workers)]
		(loop [workers sorted]
		  (if (empty? workers)
		    (throw (NotImplementedException.))
		    (let [worker (first workers)
			  dist (connected-workers worker)]
		      (send worker-loads (fn [l] (assoc l worker (inc (l worker)))))
		      (let [result (apply (worker-function (connected-workers worker) fun (fn [& _] ::local)) args)]
			(send worker-loads (fn [l] (assoc l worker (dec (l worker)))))
			(if (= result ::local)
			  (recur (rest workers))
			  result))))))))))
