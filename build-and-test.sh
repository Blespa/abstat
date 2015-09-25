#!/bin/bash

set -e

function devtest(){
	./abstat.sh build
	./abstat.sh run --dry testing/test-java-ui-module.sh
	./abstat.sh run --dry testing/test-java-summarization-module.sh
	./abstat.sh run testing/end2end-test.sh

	./install.sh
	echo 'waiting all services to start up'
	sleep 10
	./abstat.sh exec testing/system-tests.sh
	./abstat.sh destroy

	./abstat.sh run --dry "rm -rf data/logs"
}

function integrationtest(){
	./abstat.sh build
	testing/test-java-ui-module.sh 
	testing/test-java-summarization-module.sh
	./abstat.sh run testing/end2end-test.sh
	
	./abstat.sh build && ./abstat.sh destroy && ./abstat.sh run testing/system-tests.sh
	./abstat.sh build && ./abstat.sh destroy && sudo service docker restart && sleep 5 && ./abstat.sh start && sleep 20 && ./abstat.sh status | tail | grep 'Up '
}

relative_path=`dirname $0`
root=`cd $relative_path;pwd`

cd $root

case "$1" in
        development)
		devtest
                ;;
	integration)
		integrationtest
		;;
        *)
                echo "Usage: build-and-test.sh development | integration"
		;;
esac
