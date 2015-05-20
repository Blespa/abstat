#!/bin/bash

set -e

function as_absolute(){
	echo `cd $1; pwd`
}

function start(){
	docker build -t abstat startup && docker rm -f abstat && docker run -d -p 80:80 --name abstat -v $(as_absolute `dirname $0`):/schema-summaries abstat/latest
}

function stop(){
	docker stop abstat
}

case "$1" in
        start)
                start
                ;;
        stop)
                stop
                ;;
        *)
                echo "Usage: abstat start|stop"
		;;
esac
exit $status

