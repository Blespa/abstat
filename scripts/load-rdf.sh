#!/bin/bash

function as_absolute(){
	echo `cd $1; pwd`
}

set -e
relative_path=`dirname $0`
root=`cd $relative_path;pwd`

input_directory=$(as_absolute $1)
graph=$2

isql-vt 1111 dba dba VERBOSE=OFF "EXEC=delete from DB.DBA.load_list;select 'Loading data...';ld_dir ('${input_directory}', '*.nt', '${graph}');rdf_loader_run();"
