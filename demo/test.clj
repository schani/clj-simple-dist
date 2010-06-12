(vsc-fn fib 1 [n]
  (if (< n 2)
    n
    (+ (fib (- n 1)) (fib (- n 2)))))
