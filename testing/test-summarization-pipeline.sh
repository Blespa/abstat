#!/bin/bash

function assert_results_are_present_in_virtuoso(){
	sparql_query="http://localhost/sparql?default-graph-uri=http%3A%2F%2Fld-summaries.org%2Fsystem-test&query=select+count%28*%29+where+%7B%3Fa+%3Fb+%3Fc%7D&format=text%2Fplain&timeout=0&debug=on"
	expected="<http://www.w3.org/2005/sparql-results#value> \"3858\"^^<http://www.w3.org/2001/XMLSchema#integer>"

	highlight_color='\e[0;31m'
	message='KO'
	if [[ $(curl --silent "$sparql_query" | grep "$expected") ]]
	then
		highlight_color='\e[0;32m'
		message="OK"
	fi
	echo -e "checking that rdf produced was loaded: ${highlight_color}${message}\e[0m"
}

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

set -e
relative_path=`dirname $0`
current_directory=$(as_absolute $relative_path)
root=$(as_absolute $current_directory/../)

results=$root/data/summaries/system-test
expected_results=$root/benchmark/regression-test-results

cd $current_directory
cd ../pipeline
./run-summarization-pipeline.sh system-test
assert_no_errors_on ../summarization/log/log.txt
assert_results_are_compliant $expected_results $results
./export-to-rdf.sh system-test
assert_results_are_present_in_virtuoso
cd ../testing

