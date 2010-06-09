#!/bin/bash

exec java -Dpid=$$ -cp `echo lib/*.jar | sed -e 's/ /:/g'`:classes:src/clj clojure.main -i demo/testworker.clj
