simple-dist is a very simple job distribution service for
[Clojure](http://clojure.org).

simple-dist was written under two assumptions: The jobs are pure
functions, i.e. they have no side effects and are only run to produce
a result, and they run for at least some non-trivial amount of time,
like one second.

simple-dist requires Clojure 1.2.

Demo
----

If you don't have it already, install
[leiningen](http://github.com/technomancy/leiningen).  Download
dependencies and compile Java code:

    lein deps
    lein compile-java

With everything in place, run a worker:

    ./demo/runworker.sh

The worker defines a few functions, like "fib" to calculate Fibonacci
numbers, and provides an interface to call those functions remotely.

Now run a distributor:

    ./demo/rundistributor.sh

A distributor provides the same interface as a worker, but doesn't do
the work itself.  Instead it distributes the jobs to one or more
workers that it connects to.

Now, in a Clojure REPL we can do:

    (use 'at.ac.tuwien.complang.distributor.client)
    (def dist (connect "localhost" 1099))

This connects to the distributor, which uses port 1099.  Then, let's
get a handle on the "fib" function:

    (def dist-fib (worker-function dist "fib" (fn [x] 'local)))

The third argument to "worker-function" should actually be the local
"fib" function - if the connection dies or the worker doesn't
implement the "fib" function, the local function is used instead.
Here we use a different function to see whether or not the job is
processed remotely or locally.

Let's call it:

    (dist-fib 20)
    => 6765

Wonderful!  Just to see if it will still work, kill the worker
process, and try again:

    (dist-fib 20)
    => local

Now restart the worker process and try again:

    (dist-fib 20)
    => 6765

Run a few long-running jobs:

    (pmap dist-fib (range 45))

and go to [http://localhost:8080](http://localhost:8080).  There
you'll see the simple web interface of the distributor that tells you
which workers it knows about and what jobs they're currently
executing.

Have fun!
