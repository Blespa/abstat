#!/bin/bash

set -e

function test_installation(){
	$1
	echo 'Waiting all services to start up'
	sleep 20
	./abstat.sh status | tail | grep 'Up '
}

function mime_installation(){
	./abstat.sh build 
	./abstat.sh destroy 
	sudo service docker restart 
	sleep 5
	./abstat.sh start
}

function unit_tests(){
	$@ testing/test-java-ui-module.sh
	$@ testing/test-java-summarization-module.sh
	./abstat.sh run testing/end2end-test.sh
	./abstat.sh run testing/system-tests.sh	
}

relative_path=`dirname $0`
root=`cd $relative_path;pwd`
cd $root

case "$1" in
        development)
		unittest_runner='./abstat.sh run --dry'
		installation='./install.sh'
                ;;
	integration)
		installation='mime_installation'
		;;
esac

./abstat.sh build
unit_tests $unittest_runner
./abstat.sh destroy
test_installation $installation
./abstat.sh run --dry "rm -rf data/logs"
./abstat.sh destroy

