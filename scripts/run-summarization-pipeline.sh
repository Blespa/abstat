#!/bin/bash

set -e
relative_path=`dirname $0`
root=`cd $relative_path;pwd`

cd "$root/../summarization/Utility/"
./extract_info.sh

