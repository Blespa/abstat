#!/bin/bash

function as_absolute(){
	echo `cd $1; pwd`
}

set -e
relative_path=`dirname $0`
current_directory=$(as_absolute $relative_path)

cd $current_directory

root=$(as_absolute $current_directory/../)
data=$root/test-data
results=$root/summarization-results

./run-summarization-pipeline.sh $data $results

