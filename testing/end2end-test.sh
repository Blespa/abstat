#!/bin/bash

function as_absolute(){
	echo `cd $1; pwd`
}

set -e
relative_path=`dirname $0`
current_directory=$(as_absolute $relative_path)

cd $current_directory
echo "SYSTEM TEST"
./check-system-configuration.sh
./test-java-ui-module.sh
./test-java-summarization-module.sh
./test-summarization-pipeline.sh
./test-indexing-pipeline.sh
./system-tests.sh

