#!/bin/bash

function as_absolute(){
	echo `cd $1; pwd`
}

function export_rdf(){
	command=$1	
	from=$2
	to=$3

	echo "exporting $from as $to"
	java -Xms256m -Xmx4g -cp .:'ontology_summarization.jar' it.unimib.disco.summarization.output.$command $from $to
	echo "done"
}

set -e
relative_path=`dirname $0`
root=`cd $relative_path;pwd`
project=$root/../summarization

mkdir -p $2

input_directory=$(as_absolute $1)/patterns
output_directory=$(as_absolute $2)
graph=$3

cd $root
./build-java-summarization-module.sh

cd $project
export_rdf WriteConceptsToRDF $input_directory/obj-patterns/countConcepts.txt $output_directory/count-concepts.nt 
export_rdf WriteAKPToRDF $input_directory/obj-patterns/relationCount.txt $output_directory/relation-count.nt 
cd $root

echo "indexing the result of the analysis in ${output_directory} into virtuoso endpoint"
./isql.sh "delete from DB.DBA.load_list;ld_dir ('${output_directory}', '*.nt', '${graph}');rdf_loader_run();"
echo "done"

rm -r $output_directory
