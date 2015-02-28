#!/bin/bash

function as_absolute(){
	echo `cd $1; pwd`
}

set -e

mkdir -p $1 $2
relative_path=`dirname $0`
current_directory=$(as_absolute $relative_path)
data=$(as_absolute $1)
results=$(as_absolute $2)

echo "building the java summarization module"

cd "$current_directory"
./build-java-summarization-module.sh

echo "done"

echo "running the summarization pipeline"
echo "with data from $data"
echo "saving results in $results"

./run-summarization.sh $data $results

echo "done"
