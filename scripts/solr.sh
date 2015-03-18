#!/bin/bash

function assert_application_is_up()
{
	url="localhost:$port/$page"

	highlight_color='\e[0;31m'
	message='KO'

	if [[ $(curl --silent $url | grep "Solr Admin") ]]
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
port=$2
page=$3

case "$1" in
        start)
		$current_directory/../solr/bin/solr start -p $port
                ;;
	assert_application_is_up)
		assert_application_is_up
		;;
	stop)
		$current_directory/../solr/bin/solr stop -p $port
                ;;
esac

echo

