#!/bin/bash

set -e
relative_path=`dirname $0`
root=`cd $relative_path;pwd`

cd "$root/../Source Code/Utility/SchemaSummaries_Data_Extraction/"
./extract_info.sh

