#!/bin/bash

set -e

function as_absolute(){
	echo `cd $1; pwd`
}

function start(){
	docker rm -f abstat && docker run -d -p 80:80 --name abstat -v $(as_absolute `dirname $0`):/schema-summaries abstat/latest
}

function stop(){
	docker stop abstat
}

function run(){
	docker exec abstat /schema-summaries/$1
}

function build(){
	docker build -t abstat startup
	docker run -it -v $(as_absolute `dirname $0`):/schema-summaries abstat/latest /schema-summaries/build/build-java-summarization-module.sh
	docker run -it -v $(as_absolute `dirname $0`):/schema-summaries abstat/latest /schema-summaries/build/build-java-ui-module.sh
}

case "$1" in
        start)
                start
                ;;
        stop)
                stop
                ;;
	build)
		build
		;;
	run)
		run $2
		;;
        *)
                echo "Usage: abstat start|stop|build|run"
		;;
esac
exit $status

