#!/bin/bash

function assert_application_is_up(){

	port=$1
	page=$2
	expected_response=$3

	url="localhost:$port/$page"

	highlight_color='\e[0;31m'
	message='KO'

	if [[ $(curl --silent $url | grep "$expected_response") ]]
	then
		highlight_color='\e[0;32m'
		message="OK"
	fi
	echo -e "checking that $url is up: ${highlight_color}${message}\e[0m"
}

function as_absolute(){
	echo `cd $1; pwd`
}

set -e
relative_path=`dirname $0`
current_directory=$(as_absolute $relative_path)

cd $current_directory

echo "integration testing of the solr module"
solr_port=8886
echo
./solr.sh start $solr_port

assert_application_is_up $solr_port solr/ "Solr Admin"

cd ../summarization
java -Xms256m -Xmx4g -cp .:'ontology_summarization.jar' it.unimib.disco.summarization.output.IndexConcepts localhost $solr_port ../benchmark/regression-test-results/patterns/obj-patterns/countConcepts.txt
cd ../scripts

highlight_color='\e[0;31m'
message='KO'
if [[ $(curl --silent http://localhost:$solr_port/solr/indexing/select?q=*:*&fq=type:concept | grep "numFound=\"778\"") ]]
then
	highlight_color='\e[0;32m'
	message="OK"
fi

echo -e "checking that solr indexing concepts was successful: ${highlight_color}${message}\e[0m"

cd ../summarization
java -Xms256m -Xmx4g -cp .:'ontology_summarization.jar' it.unimib.disco.summarization.output.IndexProperties localhost $solr_port ../benchmark/regression-test-results/patterns/obj-patterns/countProp.txt
cd ../scripts

highlight_color='\e[0;31m'
message='KO'
if [[ $(curl --silent http://localhost:$solr_port/solr/indexing/select?q=*:*&fq=type:property | grep "numFound=\"213\"") ]]
then
	highlight_color='\e[0;32m'
	message="OK"
fi

echo -e "checking that solr indexing properties was successful: ${highlight_color}${message}\e[0m"

echo
./solr.sh stop $solr_port

