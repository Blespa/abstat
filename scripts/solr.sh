#!/bin/bash

function as_absolute()
{
	echo `cd $1; pwd`
}

set -e
relative_path=`dirname $0`
current_directory=$(as_absolute $relative_path)
port=$2
page=$3

$current_directory/../solr/bin/solr $1 -p $port

