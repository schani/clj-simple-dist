(ns at.ac.tuwien.complang.distributor.webserver
  (:use compojure.core
	ring.adapter.jetty
	clojure.contrib.def
	at.ac.tuwien.complang.distributor.distributor)
  (:require [compojure.route :as route]))

(defn- main-page [dist]
  (apply str
	 "<h1>Distributor</h1>"
	 "<table><tr><td><b>host</b></td><td><b>port</b></td><td><b>load</b></td></tr>"
	 (apply str (map (fn [x]
			   (let [worker (key x)
				 load (val x)]
			     (str "<tr><td>" (:host worker) "</td><td>" (:port worker) "</td><td>" load "</td></tr>")))
			 (loads dist)))
	 "</table>"))

(defn distributor-routes [dist]
  (routes
   (GET "/" [] (main-page dist))
   (route/not-found "<h1>Page not found</h1>")))

;(run-jetty main-routes {:port 8008})
