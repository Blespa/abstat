#!/bin/bash

set -e
relative_path=`dirname $0`
current_directory=`cd $relative_path;pwd`
root=$current_directory/..

cd "$root/summarization/Utility/"

./extract_info.sh $root/test-data

