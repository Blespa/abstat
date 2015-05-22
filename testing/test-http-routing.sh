#!/bin/bash

function assert_application_is_up(){

	page=$1
	expected_response=$2

	url="localhost${page}"

	highlight_color='\e[0;31m'
	message='KO'

	if [[ $(curl --location --silent $url | grep "$expected_response") ]]
	then
		highlight_color='\e[0;32m'
		message="OK"
	fi
	echo -e "checking that $url is up: ${highlight_color}${message}\e[0m"
}

set -e

echo "integration testing of the web interface module"
assert_application_is_up '/' 'ABSTAT'
assert_application_is_up ':8880/' 'ABSTAT'
assert_application_is_up '/sparql' 'SPARQL'
assert_application_is_up '/describe/?uri=http%3A%2F%2Fld-summaries.org%2Fresource%2Fdbpedia-2014%2Fwww.ontologydesignpatterns.org%2Font%2Fdul%2FDUL.owl%23NaturalPerson' 'About:'

echo "integration testing of the solr module"
assert_application_is_up '/solr/' 'Solr Admin'
