(ns at.ac.tuwien.complang.distributor.webserver
  (:use compojure.core
	ring.adapter.jetty
	hiccup
	clojure.contrib.def
	at.ac.tuwien.complang.distributor.distributor)
  (:require [compojure.route :as route]))

(defn- main-page [dist]
  (html [:h1 "Distributor"]
	[:table [:tr (map (fn [x] [:td [:b x]]) ["host" "port" "load"])]
	 (map (fn [x]
		(let [worker (key x)
		      load-map (val x)]
		  [:tr [:td (:host worker)] [:td (:port worker)] [:td (count load-map)]]))
	      (loads dist))]))

(defn distributor-routes [dist]
  (routes
   (GET "/" [] (main-page dist))
   (route/not-found "<h1>Page not found</h1>")))
