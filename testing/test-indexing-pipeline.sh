#!/bin/bash

function as_absolute()
{
	echo `cd $1; pwd`
}

set -e
relative_path=`dirname $0`
current_directory=$(as_absolute $relative_path)
cd $current_directory

dataset=$1

echo
echo "indexing the produced summary"

solr_port=8891

#Delete all the documents that are in the index.
cd ../summarization
java -Xms256m -Xmx4g -cp .:'ontology_summarization.jar' it.unimib.disco.summarization.output.DeleteAllDocumentsIntoIndex localhost $solr_port $dataset
cd ../testing

sleep 1

cd ../summarization
java -Xms256m -Xmx4g -cp .:'ontology_summarization.jar' it.unimib.disco.summarization.output.IndexConcepts localhost $solr_port ../data/summaries/$dataset/patterns/count-concepts.txt $dataset
cd ../testing

cd ../summarization
java -Xms256m -Xmx4g -cp .:'ontology_summarization.jar' it.unimib.disco.summarization.output.IndexDatatypeProperties localhost $solr_port ../data/summaries/$dataset/patterns/count-datatype-properties.txt $dataset
cd ../testing

cd ../summarization
java -Xms256m -Xmx4g -cp .:'ontology_summarization.jar' it.unimib.disco.summarization.output.IndexObjectProperties localhost $solr_port ../data/summaries/$dataset/patterns/count-object-properties.txt $dataset
cd ../testing

cd ../summarization
java -Xms256m -Xmx4g -cp .:'ontology_summarization.jar' it.unimib.disco.summarization.output.IndexDatatypeAkps localhost $solr_port ../data/summaries/$dataset/patterns/datatype-akp.txt $dataset
cd ../testing

cd ../summarization
java -Xms256m -Xmx4g -cp .:'ontology_summarization.jar' it.unimib.disco.summarization.output.IndexObjectAkps localhost $solr_port ../data/summaries/$dataset/patterns/object-akp.txt $dataset
cd ../testing

