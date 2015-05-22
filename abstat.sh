#!/bin/bash

function as_absolute(){
	echo `cd $1; pwd`
}

function start(){
	default_port=80:80
	if [[ $1 == --backend ]]
	then
		default_port=8881:80
	fi
	docker run -d -p $default_port -p 8885:8885 -p 8880:8880 --name abstat -v $(as_absolute `dirname $0`):/schema-summaries abstat
}

function destroy(){
	if [[ $(docker ps | grep abstat) ]] 
	then
		docker stop abstat
	fi
	docker rm -f $(docker ps -aq)
}

function build(){
	docker build --rm -t abstat deployment
	docker run -it -v $(as_absolute `dirname $0`):/schema-summaries abstat /schema-summaries/build/build-java-summarization-module.sh
	docker run -it -v $(as_absolute `dirname $0`):/schema-summaries abstat /schema-summaries/build/build-java-ui-module.sh
	docker run -it -v $(as_absolute `dirname $0`):/schema-summaries abstat chmod 775 -R /schema-summaries/web/log
	docker run -it -v $(as_absolute `dirname $0`):/schema-summaries abstat chmod 775 -R /schema-summaries/summarization/log
	docker run -it -v $(as_absolute `dirname $0`):/schema-summaries abstat chmod 775 -R /schema-summaries/data/
}

function run(){
	command=$1
	shift
	docker exec abstat /schema-summaries/$command $@
}

function status(){
	set +e
	docker ps -a | grep abstat
	echo
	docker inspect abstat
	set -e
}

function log(){
	docker logs abstat
}

set -e

case "$1" in
        start)
		shift
                start $1
                ;;
        destroy)
                destroy
                ;;
	build)
		build
		;;
	run)
		shift
		run $@
		;;
	status)
		status
		;;
	log)
		log
		;;
        *)
                echo "Usage: abstat start | destroy | build | run | status | log"
		;;
esac
exit $status

