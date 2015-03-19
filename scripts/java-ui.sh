#!/bin/bash

set -e

function start(){
	log_begin_msg "starting summarization-ui service on port $port"
	start-stop-daemon --chuid $current_user --start --background --exec "/usr/bin/java" -m --pidfile "$pid" -d $project -- -cp .:"summarization-web.jar" it.unimib.disco.summarization.web.WebApplication $port
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

relative_path=`dirname $0`
root=`cd $relative_path;pwd`
project=$root/../web
current_user=$(id -u -n)
if [[ $current_user == 'root' ]]
then
	current_user='schema-summaries'
fi
port=$2
if [[ $port == '' ]]
then
	port=8880
fi
pid=log/java-ui-$port.pid
status=0

. /lib/lsb/init-functions

cd $project

case "$1" in
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
exit $status

