#! /bin/bash

set -e

target_directory=/schema-summaries/data/datasets/linked-brainz
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
