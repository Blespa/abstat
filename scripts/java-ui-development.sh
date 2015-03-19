#!/bin/bash

relative_path=`dirname $0`
root=`cd $relative_path;pwd`

cd $root

./java-ui.sh $@
