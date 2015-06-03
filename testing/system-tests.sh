#!/bin/bash

set -e
relative_path=`dirname $0`
root=`cd $relative_path;pwd`
project=$root/../system-tests

echo running the system tests
cd $project
java -Xms256m -Xmx1g -cp .:'system-tests.jar' org.junit.runner.JUnitCore it.unimib.disco.summarization.systemTests.TestSuite
cd $root

