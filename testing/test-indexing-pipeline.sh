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
echo "integration testing of the solr module"
echo

solr_port=8891

#Delete all the documents that are in the index.
cd ../summarization
java -Xms256m -Xmx4g -cp .:'ontology_summarization.jar' it.unimib.disco.summarization.output.DeleteAllDocumentsIntoIndex localhost $solr_port
cd ../testing

sleep 1
#Indexing concepts and checking that are indexed correctly.

cd ../summarization
java -Xms256m -Xmx4g -cp .:'ontology_summarization.jar' it.unimib.disco.summarization.output.IndexConcepts localhost $solr_port ../data/summaries/system-test/patterns/count-concepts.txt
cd ../testing

highlight_color='\e[0;31m'
message='KO'
if [[ $(curl --silent http://localhost:$solr_port/solr/indexing/select?q=type:concept | grep "numFound=\"20\"") ]]
then
	highlight_color='\e[0;32m'
	message="OK"
fi

echo -e "checking that solr indexing concepts was successful: ${highlight_color}${message}\e[0m"
echo

#Indexing Datatype-properties and checking that are indexed correctly.

cd ../summarization
java -Xms256m -Xmx4g -cp .:'ontology_summarization.jar' it.unimib.disco.summarization.output.IndexDatatypeProperties localhost $solr_port ../data/summaries/system-test/patterns/count-datatype-properties.txt
cd ../testing

highlight_color='\e[0;31m'
message='KO'
if [[ $(curl --silent http://localhost:$solr_port/solr/indexing/select?q=type:datatypeProperty | grep "numFound=\"11\"") ]]
then
	highlight_color='\e[0;32m'
	message="OK"
fi

echo -e "checking that solr indexing datatype properties was successful: ${highlight_color}${message}\e[0m"

#Indexing Object-properties and checking that are indexed correctly.

cd ../summarization
java -Xms256m -Xmx4g -cp .:'ontology_summarization.jar' it.unimib.disco.summarization.output.IndexObjectProperties localhost $solr_port ../data/summaries/system-test/patterns/count-object-properties.txt
cd ../testing

highlight_color='\e[0;31m'
message='KO'
if [[ $(curl --silent http://localhost:$solr_port/solr/indexing/select?q=type:objectProperty | grep "numFound=\"5\"") ]]
then
	highlight_color='\e[0;32m'
	message="OK"
fi

echo -e "checking that solr indexing object properties was successful: ${highlight_color}${message}\e[0m"

#Checking that all properties are indexed correctly.

highlight_color='\e[0;31m'
message='KO'
if [[ $(curl --silent http://localhost:$solr_port/solr/indexing/select?q=type:datatypeProperty+OR+type:ObjectProperty | grep "numFound=\"16\"") ]]
then
	highlight_color='\e[0;32m'
	message='OK'
fi

echo -e "checking that solr indexing all properties was successful: ${highlight_color}${message}\e[0;m"
echo

#Indexing datatype akps and checking that are indexed correctly.

cd ../summarization
java -Xms256m -Xmx4g -cp .:'ontology_summarization.jar' it.unimib.disco.summarization.output.IndexDatatypeAkps localhost $solr_port ../data/summaries/system-test/patterns/datatype-akp.txt
cd ../testing

highlight_color='\e[0;31m'
message='KO'
if [[ $(curl --silent http://localhost:$solr_port/solr/indexing/select?q=type:datatypeAkp | grep "numFound=\"68\"") ]]
then
	highlight_color='\e[0;32m'
	message='OK'
fi

echo -e "checking that solr indexing datatype akps was successful: ${highlight_color}${message}\e[0m"

#Indexing object akps and checking that are indexed correctly.

cd ../summarization
java -Xms256m -Xmx4g -cp .:'ontology_summarization.jar' it.unimib.disco.summarization.output.IndexObjectAkps localhost $solr_port ../data/summaries/system-test/patterns/object-akp.txt
cd ../testing

highlight_color='\e[0;31m'
message='KO'
if [[ $(curl --silent http://localhost:$solr_port/solr/indexing/select?q=type:objectAkp | grep "numFound=\"109\"") ]]
then
	highlight_color='\e[0;32m'
	message='OK'
fi

echo -e "checking that solr indexing object akps was successful: ${highlight_color}${message}\e[0m"

#Checking that all akps are indexed correctly.

highlight_color='\e[0;31m'
message='KO'
if [[ $(curl --silent http://localhost:$solr_port/solr/indexing/select?q=type:datatypeAkp+OR+type:ObjectAkp | grep "numFound=\"177\"") ]]
then
	highlight_color='\e[0;32m'
	message='OK'
fi

echo -e "checking that solr indexing all akps was successful: ${highlight_color}${message}\e[0;m"
echo

#Checking that all documents are indexed correctly.

highlight_color='\e[0;31m'
message='KO'
if [[ $(curl --silent http://localhost:$solr_port/solr/indexing/select?q=*:* | grep "numFound=\"213\"") ]]
then
	highlight_color='\e[0;32m'
	message='OK'
fi

echo -e "checking that solr indexing was successful: ${highlight_color}${message}\e[0;m"
echo

