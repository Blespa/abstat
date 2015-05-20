#! /bin/bash

set -e
relative_path=`dirname $0`
root=`cd $relative_path;pwd`

cd $root
user=$1

echo deploying the summarization module
./deploy.sh $user@193.204.59.21
echo "done"

echo deploying the webapp module
./deploy.sh $user@10.109.149.57
echo "done"
