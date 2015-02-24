#!/bin/bash

function as_absolute(){
	echo `cd $1; pwd`
}

set -e
relative_path=`dirname $0`
current_directory=$(as_absolute $relative_path)

echo "building the java summarization module"

cd "$current_directory"
./build-java-summarization-module.sh

echo "done"

echo "running the summarization pipeline"

root=$(as_absolute $current_directory/../)
data=$root/test-data
results=$root/summarization-results

echo "with data from $data"
echo "saving results in $results"

./run-summarization.sh $data $results

echo "done"

