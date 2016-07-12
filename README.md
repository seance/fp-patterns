# Functional Programming Patterns Tutorial

A tutorial and simple learning platform for studying basic functional patterns.

## Getting started

**Prerequisites**

You'll need Java and Git.

**Running**

* Make sure that the ports 8000, 8001, 2552 and 2553 are available.

* Run `./meditate <spec>` (use `meditate.bat` on Windows) from the command line at the root of the repo. Here `<spec>` is the class name of a specification, for example `Options`. Currently there are specs for `Options`, `Eithers`, `Lists`, `Leo` and `Futures`.

* Open your browser to localhost:8000, and select the tutorial matching the specification for your meditate command.

* In the specification (found in specs/src/test/scala), replace the placeholder symbols (`__`) with code that completes the example. As you complete examples, the web page will give you feedback in real time, and reveal more content for reading.

* When you complete a tutorial (like Options), terminate the `meditate` process running in the console simply by pressing enter (on Windows, close both opened command prompt windows). To start with the next tutorial, run the corresponding `meditate` command.

Note that compile errors in the specification will not be relayed to the web page. Keep an eye on the console window where you run the specs. If there are compile errors, use the compiler's error messages to help you fix them as usual.
