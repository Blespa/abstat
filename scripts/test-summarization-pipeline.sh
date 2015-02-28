#!/bin/bash

function assert_no_errors_on()
{
	file=$1
	
	highlight_color='\e[0;32m'
	message='TEST PASSED'

	if [[ $(grep "Command exited with non-zero status 1" $file) ]]
	then
		highlight_color='\e[0;31m'
		message="TEST FAILED: there were errors in log file ${file}"
	fi
	echo
	echo -e "${highlight_color}${message}"
	echo
}

function as_absolute(){
	echo `cd $1; pwd`
}

echo
echo "RUNNING INTEGRATION TEST"
echo

set -e
relative_path=`dirname $0`
current_directory=$(as_absolute $relative_path)
root=$(as_absolute $current_directory/../)
data=$root/benchmark/integration-test
results=$root/benchmark/integration-test-results

cd $current_directory
./run-summarization-pipeline.sh $data $results
assert_no_errors_on ../summarization/log/log.txt




