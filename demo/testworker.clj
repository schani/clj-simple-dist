(use 'at.ac.tuwien.complang.distributor.convenience)

(defn fib [n]
  (if (< n 2)
    n
    (+ (fib (- n 1)) (fib (- n 2)))))

(register-worker "localhost" (.getHostName (java.net.InetAddress/getLocalHost))
		 {"fib" (fn [n] (println (str "fib " n)) (fib n))
		  "args" (fn [& args] (str args))
		  "throw" (fn [& args] (println (str "throw " args)) (throw (Exception. (str args))))})
