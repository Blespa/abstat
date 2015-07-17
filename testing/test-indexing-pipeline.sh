#!/bin/bash

function as_absolute(){
	echo `cd $1; pwd`
}

set -e
relative_path=`dirname $0`
current_directory=$(as_absolute $relative_path)

cd $current_directory
cd ../pipeline

./export-to-solr.sh system-test dbpedia.org

cd ../testing
