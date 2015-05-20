#!/bin/bash

set -e

relative_path=`dirname $0`
root=`cd $relative_path;pwd`

cd $root

./abstat.sh build
./abstat.sh destroy
./abstat.sh start

