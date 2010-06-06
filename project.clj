(defproject dist "0.0.1-SNAPSHOT"
  :description "Simple Clojure Distribution Service"
  :dependencies [[org.clojure/clojure "1.2.0-master-SNAPSHOT"]
                 [org.clojure/clojure-contrib "1.2.0-SNAPSHOT"]
		 [ring/ring "0.2.2"]
		 [compojure "0.4.0-RC3"]
		 [hiccup "0.4.0-SNAPSHOT"]]
  :source-path "src/clj"
  :java-source-path "src/java"
  :dev-dependencies [[lein-javac "0.0.2-SNAPSHOT"]
		     [swank-clojure "1.2.1"]])
