#!/bin/bash

java -cp `echo lib/*.jar | sed -e 's/ /:/g'`:classes:src/clj clojure.main -i testworker.clj
