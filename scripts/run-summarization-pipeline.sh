#!/bin/bash

function as_absolute(){
	echo `cd $1; pwd`
}

set -e
relative_path=`dirname $0`
current_directory=$(as_absolute $relative_path)

cd "$current_directory/../summarization"

root=$(as_absolute $current_directory/../)
./extract_info.sh $root/test-data/ $root/summarization-results/

