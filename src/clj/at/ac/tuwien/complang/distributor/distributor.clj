(ns at.ac.tuwien.complang.distributor.distributor
  (:import [at.ac.tuwien.complang.distributor Distributor NotImplementedException])
  (:use at.ac.tuwien.complang.distributor.client))

(defn- make-counter []
  (let [c (atom 0)]
    #(swap! c inc)))

(defprotocol DistributorAccess
  (loads [this]))

(defn- add-load [workers name load-id fun time]
  (let [worker (workers name)]
    ;; the worker might have been removed already
    (if worker
      (let [new-worker (assoc worker :loads (assoc (:loads worker) load-id {:function fun :start-time time}))]
	(assoc workers name new-worker))
      workers)))

(defn- remove-load [workers name load-id]
  (let [worker (workers name)]
    (if worker
      (let [new-worker (assoc worker :loads (dissoc (:loads worker) load-id))]
	(assoc workers name new-worker))
      workers)))

;; workers maps from name to a map {:worker <worker-obj> :loads <load-map>}
;; where load-map maps from the load-id to a map {:function <fun-name> :start-time <time>}
(defn distributor-server []
  (let [workers (agent {})
	counter (make-counter)]
    (reify
     Distributor
     (register [this worker name]
	       (send workers assoc name {:worker worker :loads {}}))
     (compute [this fun args]
	      (let [sorted (sort-by #(count (:loads %)) @workers)]
		(loop [sorted sorted]
		  (if (empty? sorted)
		    (throw (NotImplementedException.))
		    (let [[name worker] (first sorted)
			  dist (:worker worker)
			  load-id (counter)]
		      (send workers add-load name load-id fun (System/currentTimeMillis))
		      (let [result (try
				    (.compute dist fun args)
				    (catch NotImplementedException exc
				      ::not-implemented)
				    (catch java.rmi.UnexpectedException exc
				      (throw (or (.getCause exc) exc)))
				    (catch Exception _
				      ::failure)
				    (finally
				     (send workers remove-load name load-id)))]
			(case result
			      ::not-implemented
			      (recur (rest sorted))
			      ::failure
			      (do
				(send workers dissoc name)
				(recur (rest sorted)))
			      result)))))))
     DistributorAccess
     (loads [this]
	    (into {} (map (fn [kv] [(key kv) (:loads (val kv))])
			  @workers))))))
