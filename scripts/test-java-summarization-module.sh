#!/bin/bash

set -e
relative_path=`dirname $0`
root=`cd $relative_path;pwd`
project=$root/../summarization

echo unit testing the java summarization module
cd $root
./build-java-summarization-module.sh

cd $project/bin
java -Xms256m -Xmx1g -cp .:'ontology_summarization.jar' org.junit.runner.JUnitCore it.unimib.disco.summarization.tests.TestSuite
cd $root

