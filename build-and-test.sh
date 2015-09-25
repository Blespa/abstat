#!/bin/bash

set -e

relative_path=`dirname $0`
root=`cd $relative_path;pwd`

cd $root

./abstat.sh build
./abstat.sh run --dry /schema-summaries/testing/test-java-ui-module.sh
./abstat.sh run --dry /schema-summaries/testing/test-java-summarization-module.sh
./abstat.sh run /schema-summaries/testing/end2end-test.sh
./abstat.sh destroy

./abstat.sh build
./abstat.sh run /schema-summaries/testing/system-tests.sh
./abstat.sh run --dry "rm -rf /schema-summaries/data/logs"
./abstat.sh destroy
