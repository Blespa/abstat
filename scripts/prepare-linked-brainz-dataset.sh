#! /bin/bash

set -e

target_directory=$1

if [[ $target_directory == '' ]]
then
	echo "No directory passed as argument. Please specify the directory were the data have to be saved."
	exit 1
fi

rm -rf $target_directory
mkdir -p $target_directory

ontology_directory=$target_directory/ontology
mkdir $ontology_directory
wget --header='Accept:application/rdf+xml' http://musicontology.com/specification/ -O $ontology_directory/mo.owl

triples_directory=$target_directory/triples
mkdir -p $triples_directory
wget -r -nd --no-parent -A gz http://linkedbrainz.org/rdf/dumps/20140831/ -P $triples_directory
ls $triples_directory | while read file
do
	gunzip -d $triples_directory/$file
done

cat $triples_directory/*.nt | grep -v "# started" > $triples_directory/dataset
rm $triples_directory/*.nt
mv $triples_directory/dataset $triples_directory/dataset.nt
