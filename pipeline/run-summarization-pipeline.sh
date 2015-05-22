#!/bin/bash

function as_absolute(){
	echo `cd $1; pwd`
}

set -e
relative_path=`dirname $0`
current_directory=`cd $relative_path;pwd`

dataset=$1

data=$(as_absolute $current_directory/../data/datasets/$dataset)
results=$current_directory/../data/summaries/$dataset

mkdir -p $results
results=$(as_absolute $results)

echo "running the summarization pipeline"
echo "with data from $data"
echo "saving results in $results"

cd $current_directory
./run-summarization.sh $data $results

echo "done"

