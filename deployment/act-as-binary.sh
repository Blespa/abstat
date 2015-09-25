#!/bin/bash

set -e

virtuoso='/usr/bin/virtuoso-t +wait -c deployment/virtuoso.ini'

if [[ $1 != --dry ]]
then
	mkdir -p data/logs/webapp
	mkdir -p data/logs/reverse-proxy
	mkdir -p data/logs/summarization
	mkdir -p data/logs/solr

	cp deployment/nginx.conf /etc/nginx/
	cp deployment/fct_dav.vad /usr/share/virtuoso-opensource-7/vad/ && chmod 644 /usr/share/virtuoso-opensource-7/vad/fct_dav.vad

	echo "starting up all services"
	deployment/java-ui.sh start 8892
	deployment/solr.sh start 8891
	nginx
	$virtuoso && pipeline/isql.sh "vad_install ('/usr/share/virtuoso-opensource-7/vad/fct_dav.vad', 0);" && pipeline/isql.sh "SHUTDOWN;"
	$virtuoso
	echo "all services started up"
fi

if [[ $1 == --dry ]]
then
	shift
fi

$@


