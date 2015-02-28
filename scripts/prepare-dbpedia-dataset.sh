#! /bin/bash

set -e

target_directory=$1
version=$2
include_raw=$3
dbpedia_downloads="http://downloads.dbpedia.org/${version}"

if [[ $target_directory == '' ]]
then
	echo "No directory passed as argument. Please specify the directory were the data have to be saved."
	exit 1
fi

rm -rf $target_directory
mkdir -p $target_directory

ontology_directory=$target_directory/ontology
mkdir $ontology_directory
wget "$dbpedia_downloads/dbpedia_$version.owl.bz2" -P $ontology_directory
bunzip2 "$ontology_directory/dbpedia_$version.owl.bz2"

triples_directory=$target_directory/triples
wget "$dbpedia_downloads/en/instance_types_en.nt.bz2" -P $triples_directory
bunzip2 $triples_directory/instance_types_en.nt.bz2
wget "$dbpedia_downloads/en/mappingbased_properties_en.nt.bz2" -P $triples_directory
bunzip2 $triples_directory/mappingbased_properties_en.nt.bz2
wget "$dbpedia_downloads/en/persondata_en.nt.bz2" -P $triples_directory
bunzip2 $triples_directory/persondata_en.nt.bz2
wget "$dbpedia_downloads/en/specific_mappingbased_properties_en.nt.bz2" -P $triples_directory
bunzip2 $triples_directory/specific_mappingbased_properties_en.nt.bz2
if [[ $include_raw != --raw-properties ]]
then
	wget "$dbpedia_downloads/en/raw_infobox_properties_en.nt.bz2"
	bunzip2 $triples_directory/raw_infobox_properties_en.nt.bz2
fi

cat $triples_directory/*.nt | grep -v "# started" > $triples_directory/dataset
rm $triples_directory/*.nt
mv $triples_directory/dataset $triples_directory/dataset.nt


