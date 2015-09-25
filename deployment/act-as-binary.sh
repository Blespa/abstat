#!/bin/bash

set -e

if [[ $1 != --dry ]]
then
	mkdir -p /schema-summaries/data/logs/webapp
	mkdir -p /schema-summaries/data/logs/reverse-proxy
	mkdir -p /schema-summaries/data/logs/summarization
	mkdir -p /schema-summaries/data/logs/solr
	echo "starting up all services"
	./java-ui.sh start 8892
	./solr.sh start 8891
	nginx
	/usr/bin/virtuoso-t +wait && /usr/bin/isql-vt 1111 dba dba "EXEC=vad_install ('/usr/share/virtuoso-opensource-7/vad/fct_dav.vad', 0);" && /usr/bin/isql-vt 1111 dba dba "EXEC=SHUTDOWN;"
	/usr/bin/virtuoso-t +wait
	echo "all services started up"
fi

if [[ $1 == --dry ]]
then
	shift
fi

$@


