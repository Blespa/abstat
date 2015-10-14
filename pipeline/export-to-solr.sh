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
dataset=$1
payleveldomain=$2

cd ../summarization

java -Xms256m -Xmx4g -cp .:'ontology_summarization.jar' it.unimib.disco.summarization.output.InternalExternalConcept ../data/summaries/$dataset/patterns/count-concepts.txt $dataset $payleveldomain
java -Xms256m -Xmx4g -cp .:'ontology_summarization.jar' it.unimib.disco.summarization.output.InternalExternalConcept ../data/summaries/$dataset/patterns/count-datatype.txt $dataset $payleveldomain
java -Xms256m -Xmx4g -cp .:'ontology_summarization.jar' it.unimib.disco.summarization.output.InternalExternalDatatypeProperty ../data/summaries/$dataset/patterns/count-datatype-properties.txt $dataset $payleveldomain
java -Xms256m -Xmx4g -cp .:'ontology_summarization.jar' it.unimib.disco.summarization.output.InternalExternalObjectProperty ../data/summaries/$dataset/patterns/count-object-properties.txt $dataset $payleveldomain
java -Xms256m -Xmx4g -cp .:'ontology_summarization.jar' it.unimib.disco.summarization.output.InternalExternalDatatypeAkp ../data/summaries/$dataset/patterns/datatype-akp.txt $dataset $payleveldomain
java -Xms256m -Xmx4g -cp .:'ontology_summarization.jar' it.unimib.disco.summarization.output.InternalExternalObjectAkp ../data/summaries/$dataset/patterns/object-akp.txt $dataset $payleveldomain
java -Xms256m -Xmx4g -cp .:'ontology_summarization.jar' it.unimib.disco.summarization.output.DeleteAllDocumentsIntoIndex localhost $solr_port $dataset

sleep 1

java -Xms256m -Xmx4g -cp .:'ontology_summarization.jar' it.unimib.disco.summarization.output.IndexConcepts localhost $solr_port ../data/summaries/$dataset/patterns/count-concepts-new.txt $dataset
java -Xms256m -Xmx4g -cp .:'ontology_summarization.jar' it.unimib.disco.summarization.output.IndexDatatypes localhost $solr_port ../data/summaries/$dataset/patterns/count-datatype-new.txt $dataset
java -Xms256m -Xmx4g -cp .:'ontology_summarization.jar' it.unimib.disco.summarization.output.IndexDatatypeProperties localhost $solr_port ../data/summaries/$dataset/patterns/count-datatype-properties-new.txt $dataset
java -Xms256m -Xmx4g -cp .:'ontology_summarization.jar' it.unimib.disco.summarization.output.IndexObjectProperties localhost $solr_port ../data/summaries/$dataset/patterns/count-object-properties-new.txt $dataset
java -Xms256m -Xmx4g -cp .:'ontology_summarization.jar' it.unimib.disco.summarization.output.IndexDatatypeAkps localhost $solr_port ../data/summaries/$dataset/patterns/datatype-akp-new.txt $dataset
java -Xms256m -Xmx4g -cp .:'ontology_summarization.jar' it.unimib.disco.summarization.output.IndexObjectAkps localhost $solr_port ../data/summaries/$dataset/patterns/object-akp-new.txt $dataset

cd ../pipeline
