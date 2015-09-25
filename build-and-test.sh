#!/bin/bash

set -e

relative_path=`dirname $0`
root=`cd $relative_path;pwd`

cd $root

./abstat.sh build
./abstat.sh run --dry testing/test-java-ui-module.sh
./abstat.sh run --dry testing/test-java-summarization-module.sh
./abstat.sh run testing/end2end-test.sh
./abstat.sh destroy

./install.sh
echo 'waiting all services to start up'
sleep 10
./abstat.sh exec testing/system-tests.sh
./abstat.sh destroy

./abstat.sh run --dry "rm -rf data/logs"
