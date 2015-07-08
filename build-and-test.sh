#!/bin/bash

set -e

relative_path=`dirname $0`
root=`cd $relative_path;pwd`

cd $root

./install.sh
sleep 5
./abstat.sh exec testing/end2end-test.sh
./install.sh
sleep 20
./abstat.sh exec testing/system-tests.sh
./abstat.sh run "rm -rf /schema-summaries/data/logs"
./abstat.sh destroy

