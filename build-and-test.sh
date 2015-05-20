#!/bin/bash

set -e

relative_path=`dirname $0`
root=`cd $relative_path;pwd`

cd $root

./abstat.sh build
./abstat.sh start
./abstat.sh run testing/end2end-test.sh
./abstat.sh stop

