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
	docker run -v $(as_absolute `dirname $0`):/schema-summaries -d -p $default_port -p 8885:8885 -p 8880:8880 --name abstat abstat
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
	run_command /schema-summaries/build/build-java-summarization-module.sh
	run_command /schema-summaries/build/build-java-ui-module.sh
	run_command chmod 775 -R /schema-summaries/web/log
	run_command chmod 775 -R /schema-summaries/summarization/log
	run_command chmod 775 -R /schema-summaries/data/
}

function run_command(){
	docker run -v $(as_absolute `dirname $0`):/schema-summaries -it abstat $@
}

function exec_command(){
	command=$1
	shift
	docker exec abstat /schema-summaries/$command $@
}

function status(){
	set +e
	docker inspect abstat
	echo Current Status:
	docker ps -a | grep -B 1 abstat
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
	exec)
		shift
		exec_command $@
		;;
	run)
		shift
		run_command $@
		;;
	status)
		status
		;;
	log)
		log
		;;
        *)
                echo "Usage: abstat start | destroy | build | exec | run | status | log"
		;;
esac
exit $status

