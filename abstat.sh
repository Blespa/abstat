#!/bin/bash

set -e

function as_absolute(){
	echo `cd $1; pwd`
}

function start(){
	docker run -d -p 80:80 --name abstat -v $(as_absolute `dirname $0`):/schema-summaries abstat
}

function destroy(){
	docker stop abstat && docker rm -f abstat
}

function run(){
	docker exec abstat /schema-summaries/$1
}

function build(){
	docker build -t abstat deployment
	docker run -it -v $(as_absolute `dirname $0`):/schema-summaries abstat /schema-summaries/build/build-java-summarization-module.sh
	docker run -it -v $(as_absolute `dirname $0`):/schema-summaries abstat /schema-summaries/build/build-java-ui-module.sh
	docker rm $(docker ps -aq)
}

case "$1" in
        start)
                start
                ;;
        destroy)
                destroy
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

