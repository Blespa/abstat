#!/bin/bash

function as_absolute(){
	echo `cd $1; pwd`
}

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
relative_path=`dirname $0`
current_directory=$(as_absolute $relative_path)

cd $current_directory
echo "SYSTEM TEST"
./check-system-configuration.sh
./test-java-ui-module.sh
./test-java-summarization-module.sh
./test-summarization-pipeline.sh
./system-tests.sh

