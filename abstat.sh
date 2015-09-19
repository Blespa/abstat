#!/bin/bash

function as_absolute(){
	echo `cd $1; pwd`
}

function start(){
	default_port=80:80
	if [[ $1 == --backend ]]
	then
		default_port=8885:80
	fi
	$docker_command -d -p $default_port -p 8880:8880 -p 8881:8881 -p 8882:8882 --name abstat $hosts $abstat
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
	run_command chmod 775 -R /schema-summaries/data/
}

function run_command(){
	$docker_command -it $hosts $abstat $@
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

current_directory=$(as_absolute `dirname $0`)
docker_command="docker run -v $current_directory:/schema-summaries"
abstat=abstat

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

