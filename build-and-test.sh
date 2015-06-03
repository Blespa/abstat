#!/bin/bash

set -e

function assert_results_are_present_in_virtuoso(){
	sparql_query="http://localhost/sparql?default-graph-uri=http%3A%2F%2Fld-summaries.org%2Fsystem-test&query=select+count%28*%29+where+%7B%3Fa+%3Fb+%3Fc%7D&format=text%2Fplain&timeout=0&debug=on"
	expected="<http://www.w3.org/2005/sparql-results#value> \"3904\"^^<http://www.w3.org/2001/XMLSchema#integer>"

	highlight_color='\e[0;31m'
	message='KO'
	if [[ $(curl --silent "$sparql_query" | grep "$expected") ]]
	then
		highlight_color='\e[0;32m'
		message="OK"
	fi
	echo -e "checking that stored data survived the deployment: ${highlight_color}${message}\e[0m"
}

relative_path=`dirname $0`
root=`cd $relative_path;pwd`

cd $root

./install.sh
./abstat.sh exec testing/end2end-test.sh
./install.sh

sleep 20

assert_results_are_present_in_virtuoso

./abstat.sh destroy

