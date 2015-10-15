#!/bin/bash

function as_absolute()
{
	echo `cd $1; pwd`
}

function run(){
	echo "Exporting $@"
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

run InternalExternalConcept ../data/summaries/$dataset/patterns/count-concepts.txt $dataset $payleveldomain
run InternalExternalConcept ../data/summaries/$dataset/patterns/count-datatype.txt $dataset $payleveldomain
run InternalExternalDatatypeProperty ../data/summaries/$dataset/patterns/count-datatype-properties.txt $dataset $payleveldomain
run InternalExternalObjectProperty ../data/summaries/$dataset/patterns/count-object-properties.txt $dataset $payleveldomain
run InternalExternalDatatypeAkp ../data/summaries/$dataset/patterns/datatype-akp.txt $dataset $payleveldomain
run InternalExternalObjectAkp ../data/summaries/$dataset/patterns/object-akp.txt $dataset $payleveldomain
run DeleteAllDocumentsIntoIndex localhost $solr_port $dataset

sleep 1

run IndexConcepts localhost $solr_port ../data/summaries/$dataset/patterns/count-concepts-new.txt $dataset
run IndexDatatypes localhost $solr_port ../data/summaries/$dataset/patterns/count-datatype-new.txt $dataset
run IndexDatatypeProperties localhost $solr_port ../data/summaries/$dataset/patterns/count-datatype-properties-new.txt $dataset
run IndexObjectProperties localhost $solr_port ../data/summaries/$dataset/patterns/count-object-properties-new.txt $dataset
run IndexDatatypeAkps localhost $solr_port ../data/summaries/$dataset/patterns/datatype-akp-new.txt $dataset
run IndexObjectAkps localhost $solr_port ../data/summaries/$dataset/patterns/object-akp-new.txt $dataset

cd ../pipeline
