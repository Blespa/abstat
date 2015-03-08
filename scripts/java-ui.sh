#!/bin/bash

relative_path=`dirname $0`
root=`cd $relative_path;pwd`
project=$root/../web
port=$2
pid=log/java-ui-$port.pid
status=$?

. /lib/lsb/init-functions

function start(){
	log_begin_msg "starting summarization-ui service"
        java -cp .:'summarization-web.jar' it.unimib.disco.summarization.web.WebApplication $port >> /dev/null 2>&1 &
	status=$?	
	echo $! > $pid
	log_end_msg $status
}

function stop(){
	log_begin_msg "stopping summarization-ui service"
	cat $pid | xargs kill -9
	status=$?
	rm -f $pid
	log_end_msg $status
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

