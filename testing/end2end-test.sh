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

function as_absolute(){
	echo `cd $1; pwd`
}

function assert_application_is_up(){

	page=$1
	expected_response=$2

	url="localhost:${port}${page}"

	highlight_color='\e[0;31m'
	message='KO'

	if [[ $(curl --silent $url | grep "$expected_response") ]]
	then
		highlight_color='\e[0;32m'
		message="OK"
	fi
	echo -e "checking that $url is up: ${highlight_color}${message}\e[0m"
}

set -e
relative_path=`dirname $0`
current_directory=$(as_absolute $relative_path)
root=$(as_absolute $current_directory/../)
rdf_export_path=$root/summarization-output
results=$root/benchmark/tmp

cd $current_directory

echo "SYSTEM TEST"
./check-system-configuration.sh

./test-summarization-pipeline.sh

graph=http://ld-summaries.org/system-test
cd ../scripts
./export-to-rdf.sh $results $rdf_export_path $graph
cd ../testing
assert_results_are_present_in_virtuoso

./test-java-ui-module.sh

echo "integration testing of the web interface module"
assert_application_is_up / ABSTAT

echo "integration testing of the solr module"
assert_application_is_up /solr/ "Solr Admin"

