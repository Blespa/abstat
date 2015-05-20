#!/bin/bash

set -e
relative_path=`dirname $0`
root=`cd $relative_path;pwd`
project=$root/../web

echo unit testing the java web module
cd $project
java -Xms256m -Xmx1g -cp .:'summarization-web.jar' org.junit.runner.JUnitCore it.unimib.disco.summarization.web.tests.TestSuite
cd $root

