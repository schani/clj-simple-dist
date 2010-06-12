#!/bin/bash

exec java -Dat.ac.tuwien.complang.distributor.worker=true -Dpid=$$ -cp `echo lib/*.jar | sed -e 's/ /:/g'`:classes:src/clj clojure.main -i demo/testworker.clj
