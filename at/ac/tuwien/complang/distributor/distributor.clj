(ns at.ac.tuwien.complang.distributor.distributor
  (:import [at.ac.tuwien.complang.distributor DistributionServer])
  (:use at.ac.tuwien.complang.distributor.client))

;; workers is a seq of maps of the form {:host <host> :port <port>}
(defn distributor-server [workers]
  (let [connected-workers (ref {})
	worker-load (ref {})
	connect-thread-func (fn [worker]
			      (loop []
				(if (contains? @connected-workers worker)
				  (do
				    (Thread/sleep 1000)
				    (recur))
				  ;; FIXME: catch exceptions for connect
				  (let [dist (connect (:host worker) (:port worker))]
				    (dosync
				     (alter connected-workers assoc worker dist)
				     (alter worker-load assoc worker 0))))))]
    (doseq [worker workers]
      (let [thread (Thread. (fn [] (connect-thread-func worker)))]
	(.setDaemon thread true)
	(.start thread)))
    (proxy [at.ac.tuwien.complang.distributor.DistributionServer] []
      (compute [fun args]
	       (let [sorted (sort-by #(get @worker-load % 0) (keys @connected-workers))]
		 (loop [workers sorted]
		   (if (empty? workers)
		     (throw (Exception. "no workers with that function"))
		     (let [worker (first workers)
			   dist (dosync
				 (let [dist (@connected-workers worker)]
				   (when dist
				     (alter worker-load assoc worker (inc (@worker-load worker))))
				   dist))]
		       ;; FIXME: catch exceptions
		       (let [result (.compute dist fun args)]
			 (dosync
			  (when (contains? @connected-workers worker)
			    (alter worker-load assoc worker (dec (@worker-load worker)))))
			 result)))))))))

