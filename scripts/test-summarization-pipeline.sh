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

function assert_results_are_present_in_virtuoso(){
	sparql_query="http://localhost:8890/sparql?default-graph-uri=http%3A%2F%2Fsystem.test&query=select+count%28*%29+where+%7B%3Fa+%3Fb+%3Fc%7D&format=text%2Fplain&timeout=0&debug=on"
	expected="<http://www.w3.org/2005/sparql-results#value> \"2334\"^^<http://www.w3.org/2001/XMLSchema#integer>"

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

	port=$1

	highlight_color='\e[0;31m'
	message='KO'

	if [[ $(curl --silent "localhost:$port/alive" | grep "OK") ]]
	then
		highlight_color='\e[0;32m'
		message="OK"
	fi
	echo -e "checking that web ui is up: ${highlight_color}${message}\e[0m"
}

set -e
relative_path=`dirname $0`
current_directory=$(as_absolute $relative_path)
root=$(as_absolute $current_directory/../)
data=$root/benchmark/regression-test
results=$root/benchmark/tmp
expected_results=$root/benchmark/regression-test-results
rdf_export_path=$root/summarization-output

echo
echo "SYSTEM TEST"
echo

echo "checking system configuration"
virtuoso_config_file=/etc/virtuoso-opensource-6.1/virtuoso.ini
if ! command -v virtuoso-t ; then
	echo "no virtuoso end point detected"
	echo "installing via apt-get"
	echo 
	echo "\e[0;31m WARNING:\e[0m remember to set up the dba user password equal to 'dba'"	
	echo
	sudo apt-get install virtuoso-opensource virtuoso-server virtuoso-vsp-startpage virtuoso-vad-conductor
	echo
	echo "configuring virtuoso to watch ${rdf_export_path}"
	echo	
	sudo sed -i -e "s|= \., /usr/share/virtuoso-opensource-6.1/vad|= \., /usr/share/virtuoso-opensource-6.1/vad, ${rdf_export_path}|g" $virtuoso_config_file
	sudo service virtuoso-opensource-6.1 force-reload	
fi
if ! [[ $(grep $rdf_export_path $virtuoso_config_file) ]]
then
	echo "virtuoso is not configured properly:"
	echo "add ${rdf_export_path} to the DirsAllowed parameter in ${virtuoso_config_file}"
	exit
fi
echo

mkdir -p $expected_results/patterns/tmp-files

cd $current_directory
./test-java-ui-module.sh
./test-java-summarization-module.sh
./run-summarization-pipeline.sh $data $results
echo
assert_no_errors_on ../summarization/log/log.txt
assert_results_are_compliant $expected_results $results

graph=http://system.test
./isql.sh "SPARQL CLEAR GRAPH <$graph>;"
./export-to-rdf.sh $results $rdf_export_path $graph
echo
assert_results_are_present_in_virtuoso
echo

echo "integration testing of the web interface module"
port=8887
./build-java-ui-module.sh
./java-ui.sh start $port
sleep 1
assert_application_is_up $port
./java-ui.sh stop $port
echo

