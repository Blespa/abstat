#!/bin/bash

set -e
./java-ui.sh start 8870
./solr.sh start 8983
nginx
/usr/bin/virtuoso-t +wait && /usr/bin/isql-vt 1111 dba dba "EXEC=vad_install ('/usr/share/virtuoso-opensource-7/vad/fct_dav.vad', 0);" && kill $(ps aux | grep '[v]irtuoso-t' | awk '{print $2}')
/usr/bin/virtuoso-t +wait +foreground

