(ns at.ac.tuwien.complang.distributor.webserver
  (:use compojure.core
	ring.adapter.jetty
	clout.core
	hiccup
	hiccup.page-helpers
	clojure.contrib.def
	at.ac.tuwien.complang.distributor.distributor)
  (:require [compojure.route :as route]))

(defn- worker-page [dist name]
  (let [worker ((loads dist) name)]
    (if worker
      (html [:h1 "Worker " name]
	    [:table [:tr (map (fn [x] [:td [:b x]]) ["function" "time (s)"])]
	     (map (fn [x]
		    [:tr [:td (str (:function x))] [:td (int (/ (- (System/currentTimeMillis) (:start-time x)) 1000))]])
		  (map val worker))]
	    [:p (link-to "/" "Overview")])
      (html [:p "The worker " name " does not exist."]))))

(defn- main-page [dist]
  (html [:h1 "Distributor"]
	[:table [:tr (map (fn [x] [:td [:b x]]) ["worker" "load"])]
	 (map (fn [x]
		(let [[name load-map] x]
		  [:tr [:td (link-to (str "/worker/" name) name)] [:td (count load-map)]]))
	      (loads dist))]))

(defn distributor-routes [dist]
  (routes
   (GET "/" [] (main-page dist))
   (GET (route-compile "/worker/:name" {:name #"[^/]+"}) [name] (worker-page dist name))
   (route/not-found "<h1>Page not found</h1>")))
