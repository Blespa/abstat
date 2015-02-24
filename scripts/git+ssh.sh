#!/bin/bash

set -e
relative_path=`dirname $0`
current_dir=`cd $relative_path;pwd`

ssh -i $current_dir/deploy_rsa "@"
