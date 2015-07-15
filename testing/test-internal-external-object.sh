#!/bin/bash

function as_absolute(){
	echo `cd $1; pwd`
}

set -e
relative_path=`dirname $0`
current_directory=$(as_absolute $relative_path)
cd $current_directory

echo
echo "producing the files .txt that are the input for indexing"

dataset=$1

cd ../summarization
java -Xms256m -Xmx4g -cp .:'ontology_summarization.jar' it.unimib.disco.summarization.output.InternalExternalConcept ../data/summaries/$dataset/patterns/count-concepts.txt $dataset
cd ../testing

cd ../summarization
java -Xms256m -Xmx4g -cp .:'ontology_summarization.jar' it.unimib.disco.summarization.output.InternalExternalDatatypeProperty ../data/summaries/$dataset/patterns/count-datatype-properties.txt $dataset
cd ../testing

cd ../summarization
java -Xms256m -Xmx4g -cp .:'ontology_summarization.jar' it.unimib.disco.summarization.output.InternalExternalObjectProperty ../data/summaries/$dataset/patterns/count-object-properties.txt $dataset
cd ../testing

cd ../summarization
java -Xms256m -Xmx4g -cp .:'ontology_summarization.jar' it.unimib.disco.summarization.output.InternalExternalDatatypeAkp ../data/summaries/$dataset/patterns/datatype-akp.txt $dataset
cd ../testing

cd ../summarization
java -Xms256m -Xmx4g -cp .:'ontology_summarization.jar' it.unimib.disco.summarization.output.InternalExternalObjectAkp ../data/summaries/$dataset/patterns/object-akp.txt $dataset
cd ../testing
