#!/bin/bash

set -e
relative_path=`dirname $0`
root=`cd $relative_path;pwd`
project=$root/../summarization

echo unit testing the java summarization module

cd $project/bin

java -Xms256m -Xmx1g -cp .:'ontology_summarization.jar' org.junit.runner.JUnitCore it.disco.unimib.summarization.tests.TestSuite

cd $root

