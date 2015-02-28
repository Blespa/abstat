#!/bin/bash

function assert_no_errors_on()
{
	file=$1
	
	highlight_color='\e[0;32m'
	message='OK'
	
	if [[ $(grep "Command exited with non-zero status 1" $file) ]]
	then
		highlight_color='\e[0;31m'
		message="KO. There were errors in log file ${file}"
	fi
	echo -e "checking that execution was successful: ${highlight_color}${message}\e[0m"
}

function assert_results_are_compliant()
{
	expected=$1
	actual=$2
	
	highlight_color='\e[0;32m'
	message='OK'
	
	if [[ $(diff -qr $expected $actual) ]]
	then
		highlight_color='\e[0;31m'
		message="KO"
	fi
	echo -e "checking that execution produced the correct result: ${highlight_color}${message}"
	echo $(diff -qr $expected $actual)	
	echo -e "\e[0m"
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
results=$root/benchmark/tmp
expected_results=$root/benchmark/integration-test-results

cd $current_directory
./run-summarization-pipeline.sh $data $results

echo
assert_no_errors_on ../summarization/log/log.txt
assert_results_are_compliant $expected_results $results
echo



