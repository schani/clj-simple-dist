(use 'at.ac.tuwien.complang.distributor.convenience)
(use 'at.ac.tuwien.complang.distributor.webserver)
(use 'ring.adapter.jetty)

(def *dist* (register-distributor))

(run-jetty (distributor-routes *dist*) {:port 8080})
