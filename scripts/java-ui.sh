#!/bin/bash

relative_path=`dirname $0`
root=`cd $relative_path;pwd`
project=$root/../web
port=$2
pid=log/java-ui-$port.pid
status=$?

. /lib/lsb/init-functions

function start(){
	log_begin_msg "starting summarization-ui service on port $port"
	start-stop-daemon --background -m --pidfile "$pid" --start -d $project --exec "/usr/bin/java" -- -cp .:"summarization-web.jar" it.unimib.disco.summarization.web.WebApplication $port
	log_end_msg $?
}

function stop(){
	log_begin_msg "stopping summarization-ui service on port $port"
	start-stop-daemon --pidfile "$pid" --stop "/usr/bin/java" -- -cp .:'summarization-web.jar' it.unimib.disco.summarization.web.WebApplication $port
	log_end_msg $?
	rm $pid
}

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
		status=1
		;;
esac
exit $status

