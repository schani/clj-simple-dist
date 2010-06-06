(ns at.ac.tuwien.complang.distributor.distributor
  (:import [at.ac.tuwien.complang.distributor DistributionServer NotImplementedException])
  (:use at.ac.tuwien.complang.distributor.client))

(defn- make-counter []
  (let [c (atom 0)]
    #(swap! c inc)))

(defprotocol Distributor
  (loads [this]))

;; workers is a seq of maps of the form {:host <host> :port <port>}
(defn distributor-server [workers]
  (let [connected-workers (into {} (map (fn [worker] [worker (connect (:host worker) (:port worker))]) workers))
	worker-loads (agent (into {} (map (fn [worker] [worker {}]) workers)))
	counter (make-counter)]
    (reify
     DistributionServer
     (compute [this fun args]
	      (let [loads @worker-loads
		    sorted (sort-by #(loads %) workers)]
		(loop [workers sorted]
		  (if (empty? workers)
		    (throw (NotImplementedException.))
		    (let [worker (first workers)
			  dist (connected-workers worker)
			  load-id (counter)]
		      (send worker-loads (fn [l] (assoc l worker (assoc (l worker) load-id {:function fun :start-time (System/currentTimeMillis)}))))
		      (let [result (apply (worker-function (connected-workers worker) fun (fn [& _] ::local)) args)]
			(send worker-loads (fn [l] (assoc l worker (dissoc (l worker) load-id))))
			(if (= result ::local)
			  (recur (rest workers))
			  result)))))))
     Distributor
     (loads [this]
	    @worker-loads))))
