(ns at.ac.tuwien.complang.distributor.distributor
  (:import [at.ac.tuwien.complang.distributor Distributor NotImplementedException])
  (:use at.ac.tuwien.complang.distributor.client
	clojure.set))

(defn- make-counter []
  (let [c (atom 0)]
    #(swap! c inc)))

(defprotocol DistributorAccess
  (loads [this]))

(defn- select-worker! [workers-ref exclude load-id fun]
  (dosync
   (let [workers @workers-ref
	 names (difference (set (keys workers)) exclude)]
     (if (empty? names)
       nil
       (let [sorted (sort-by #(count (:loads (workers %))) names)
	     name (first sorted)
	     worker (workers name)]
	 (ref-set workers-ref (assoc workers name (assoc worker :loads (assoc (:loads worker) load-id {:function fun :start-time (System/currentTimeMillis)}))))
	 [name (workers name)])))))

(defn- remove-load! [workers-ref name load-id]
  (dosync
   (let [workers @workers-ref
	 worker (workers name)]
     (when worker
       (let [new-worker (assoc worker :loads (dissoc (:loads worker) load-id))]
	 (alter workers-ref assoc name new-worker))))))

(defn- remove-worker! [workers-ref name dist]
  (dosync
   (let [workers @workers-ref
	 worker (workers name)]
     ;; the worker object must be the same - a different worker with
     ;; the same name might have been removed and added again
     (when (and worker (= dist (:worker worker)))
       (alter workers-ref dissoc name)))))

;; workers maps from name to a map {:worker <worker-obj> :loads <load-map>}
;; where load-map maps from the load-id to a map {:function <fun-name> :start-time <time>}
(defn distributor-server []
  (let [workers (ref {})
	counter (make-counter)]
    (reify
     Distributor
     (register [this worker name]
	       (dosync
		(alter workers assoc name {:worker worker :loads {}})))
     (compute [this fun args]
	      (let [load-id (counter)]
		(loop [exclude #{}]
		  (let [[name worker] (select-worker! workers exclude load-id fun)]
		    (if worker
		      (let [dist (:worker worker)
			    result (try
				    (.compute dist fun args)
				    (catch NotImplementedException exc
				      ::not-implemented)
				    (catch java.rmi.UnexpectedException exc
				      (throw (or (.getCause exc) exc)))
				    (catch Exception _
				      ::failure)
				    (finally
				     (remove-load! workers name load-id)))]
			(case result
			      ::not-implemented
			      (recur (conj exclude name))
			      ::failure
			      (do
				(remove-worker! workers name dist)
				(recur (conj exclude name)))
			      result))
		      (throw (NotImplementedException.)))))))
     DistributorAccess
     (loads [this]
	    (into {} (map (fn [kv] [(key kv) (:loads (val kv))])
			  @workers))))))
