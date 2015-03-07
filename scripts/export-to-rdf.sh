#!/bin/bash

function as_absolute(){
	echo `cd $1; pwd`
}

set -e
relative_path=`dirname $0`
root=`cd $relative_path;pwd`
project=$root/../summarization

mkdir -p $2

input_directory=$(as_absolute $1)
output_directory=$(as_absolute $2)
graph=$3

input=$input_directory/patterns/obj-patterns/countConcepts.txt
output=$output_directory/count-concepts.nt

cd $root
./build-java-summarization-module.sh

echo "exporting $input as $output"
cd $project
java -Xms256m -Xmx1g -cp .:'ontology_summarization.jar' it.unimib.disco.summarization.output.WriteConceptsTORDF $input $output
cd $root
echo "done"

echo "indexing the result of the analysis in ${output_directory} into virtuoso endpoint"
isql-vt 1111 dba dba VERBOSE=OFF "EXEC=delete from DB.DBA.load_list;ld_dir ('${output_directory}', '*.nt', '${graph}');rdf_loader_run();"
echo "done"

rm -r $output_directory
