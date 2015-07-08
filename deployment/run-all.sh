#!/bin/bash

set -e

mkdir -p /schema-summaries/data/logs/webapp
mkdir -p /schema-summaries/data/logs/reverse-proxy
mkdir -p /schema-summaries/data/logs/summarization

./java-ui.sh start 8892
./solr.sh start 8891
nginx
/usr/bin/virtuoso-t +wait && /usr/bin/isql-vt 1111 dba dba "EXEC=vad_install ('/usr/share/virtuoso-opensource-7/vad/fct_dav.vad', 0);" && kill $(ps aux | grep '[v]irtuoso-t' | awk '{print $2}')
/usr/bin/virtuoso-t +wait +foreground

