(ns at.ac.tuwien.complang.distributor.webserver
  (:use compojure.core
	ring.adapter.jetty
	hiccup
	hiccup.page-helpers
	clojure.contrib.def
	at.ac.tuwien.complang.distributor.distributor)
  (:require [compojure.route :as route]))

(defn- worker-page [dist host port]
  (let [name (str host ":" port)
	worker ((loads dist) {:host host :port  (java.lang.Integer/parseInt port)})]
    (if worker
      (html [:h1 "Worker " name]
	    [:table [:tr (map (fn [x] [:td [:b x]]) ["function" "time (s)"])]
	     (map (fn [x]
		    [:tr [:td (:function x)] [:td (int (/ (- (System/currentTimeMillis) (:start-time x)) 1000))]])
		  (map val worker))]
	    [:p (link-to "/" "Overview")])
      (html [:p "The worker " name " does not exist."]))))

(defn- main-page [dist]
  (html [:h1 "Distributor"]
	[:table [:tr (map (fn [x] [:td [:b x]]) ["worker" "load"])]
	 (map (fn [x]
		(let [{host :host port :port} (key x)
		      load-map (val x)]
		  [:tr [:td (link-to (str "/worker/" host "/" port) (str host ":" port))] [:td (count load-map)]]))
	      (loads dist))]))

(defn distributor-routes [dist]
  (routes
   (GET "/" [] (main-page dist))
   (GET "/worker/:host/:port" [host port] (worker-page dist host port))
   (route/not-found "<h1>Page not found</h1>")))
