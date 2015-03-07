#!/bin/bash

function as_absolute(){
	readlink -f $1
}

set -e
relative_path=`dirname $0`
root=`cd $relative_path;pwd`
project=$root/../summarization

input=$(as_absolute $1)
output=$(as_absolute $2)

cd $root
./build-java-summarization-module.sh

echo "exporting $input as $output"
cd $project
java -Xms256m -Xmx1g -cp .:'ontology_summarization.jar' it.unimib.disco.summarization.output.WriteConceptsTORDF $input $output
cd $root
echo "done"

