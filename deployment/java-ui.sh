#!/bin/bash

set -e

function start(){
	log_begin_msg "starting summarization-ui service on port $port"
	start-stop-daemon --start --background --exec "/usr/bin/java" -m --pidfile "$pid" -d $project -- -cp .:"summarization-web.jar" it.unimib.disco.summarization.web.WebApplication $port
	status=$?
	log_end_msg $status
}

function stop(){
	log_begin_msg "stopping summarization-ui service on port $port"
	start-stop-daemon --oknodo --stop --pidfile "$pid"
	status=$?	
	log_end_msg $?
	rm -f $pid
}

project=/schema-summaries/summarization
command=$1
port=$2
pid=/schema-summaries/data/logs/webapp/java-ui-$port.pid
status=0

. /lib/lsb/init-functions

cd $project

case "$command" in
        start)
                start
                ;;
        stop)
                stop
                ;;
        *)
                log_success_msg "Usage: java-ui.sh start|stop"
		;;
esac
sleep 2
exit $status

