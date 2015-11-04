#!/bin/bash

function as_absolute()
{
	echo `cd $1; pwd`
}

function run(){
	echo "$@"
	java -Xms256m -Xmx16g -cp .:'summarization.jar' it.unimib.disco.summarization.export.$@
	echo "Done"
}

set -e
relative_path=`dirname $0`
current_directory=$(as_absolute $relative_path)
cd $current_directory

echo
echo "Indexing the produced summary"

solr_port=8891
dataset=$1
payleveldomain=$2

cd ../summarization

run DeleteAllDocumentsFromIndex localhost $solr_port $dataset

sleep 1

run IndexResources localhost $solr_port ../data/summaries/$dataset/patterns/count-concepts.txt $dataset concept $payleveldomain
run IndexResources localhost $solr_port ../data/summaries/$dataset/patterns/count-datatype.txt $dataset datatype $payleveldomain
run IndexResources localhost $solr_port ../data/summaries/$dataset/patterns/count-datatype-properties.txt $dataset datatypeProperty $payleveldomain
run IndexResources localhost $solr_port ../data/summaries/$dataset/patterns/count-object-properties.txt $dataset objectProperty $payleveldomain
run IndexAKP localhost $solr_port ../data/summaries/$dataset/patterns/datatype-akp.txt $dataset datatypeAkp $payleveldomain
run IndexAKP localhost $solr_port ../data/summaries/$dataset/patterns/object-akp.txt $dataset objectAkp $payleveldomain

cd ../pipeline
