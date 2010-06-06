(use 'at.ac.tuwien.complang.distributor.convenience)

(defn fib [n]
  (if (< n 2)
    n
    (+ (fib (- n 1)) (fib (- n 2)))))

(register-worker {"fib" (fn [n] (println (str "fib " n)) (fib n))
		  "args" (fn [& args] (str args))})
