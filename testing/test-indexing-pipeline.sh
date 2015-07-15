#!/bin/bash

function as_absolute()
{
	echo `cd $1; pwd`
}

set -e
relative_path=`dirname $0`
current_directory=$(as_absolute $relative_path)
cd $current_directory

echo
echo "indexing the produced summary"

solr_port=8891

#Delete all the documents that are in the index.
cd ../summarization
java -Xms256m -Xmx4g -cp .:'ontology_summarization.jar' it.unimib.disco.summarization.output.DeleteAllDocumentsIntoIndex localhost $solr_port system-test
cd ../testing

sleep 1

cd ../summarization
java -Xms256m -Xmx4g -cp .:'ontology_summarization.jar' it.unimib.disco.summarization.output.IndexConcepts localhost $solr_port ../data/summaries/system-test/patterns/count-concepts.txt system-test
cd ../testing

cd ../summarization
java -Xms256m -Xmx4g -cp .:'ontology_summarization.jar' it.unimib.disco.summarization.output.IndexDatatypeProperties localhost $solr_port ../data/summaries/system-test/patterns/count-datatype-properties.txt system-test
cd ../testing

cd ../summarization
java -Xms256m -Xmx4g -cp .:'ontology_summarization.jar' it.unimib.disco.summarization.output.IndexObjectProperties localhost $solr_port ../data/summaries/system-test/patterns/count-object-properties.txt system-test
cd ../testing

cd ../summarization
java -Xms256m -Xmx4g -cp .:'ontology_summarization.jar' it.unimib.disco.summarization.output.IndexDatatypeAkps localhost $solr_port ../data/summaries/system-test/patterns/datatype-akp.txt system-test
cd ../testing

cd ../summarization
java -Xms256m -Xmx4g -cp .:'ontology_summarization.jar' it.unimib.disco.summarization.output.IndexObjectAkps localhost $solr_port ../data/summaries/system-test/patterns/object-akp.txt system-test
cd ../testing

