#! /bin/bash

function as_absolute(){
	echo `cd $1; pwd`
}

set -e
relative_path=`dirname $0`
scripts_directory=`cd $relative_path;pwd`

target_directory=$1

if [[ $target_directory == '' ]]
then
	echo "No directory passed as argument. Please specify the directory were the data have to be saved."
	exit 1
fi
if [[ $target_directory != /* ]]
then
	echo "The path '$target_directory' passed as argument is relative. Please specify an absolute path."
	exit 1
fi

rm -rf $target_directory
mkdir -p $target_directory

triples_directory=$target_directory/triples
mkdir $triples_directory

wget "http://resources.mpi-inf.mpg.de/yago-naga/yago1_yago2/download/yago1/YAGO1.0.0/n3.zip" -P $triples_directory
unzip $triples_directory/n3.zip -d $triples_directory
rm $triples_directory/n3.zip
java -jar $scripts_directory/../tools/rdf2rdf-1.0.1-2.3.1.jar $triples_directory/yago.n3 $triples_directory/dataset.nt
rm $triples_directory/yago.n3

ontology_directory=$target_directory/ontology
mkdir -p $ontology_directory

echo "<http://mpii.de/yago/resource/wordnet_entity_100001740> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/2000/01/rdf-schema#Class> ." > $ontology_directory/yago-ontology.nt
grep "http://www.w3.org/2000/01/rdf-schema#subClassOf" $triples_directory/dataset.nt >> $ontology_directory/yago-ontology.nt

grep "http://www.w3.org/2000/01/rdf-schema#domain" $triples_directory/dataset.nt >> $ontology_directory/yago-ontology.nt
grep "http://www.w3.org/2000/01/rdf-schema#range" $triples_directory/dataset.nt >> $ontology_directory/yago-ontology.nt

java -jar $scripts_directory/../tools/rdf2rdf-1.0.1-2.3.1.jar $ontology_directory/yago-ontology.nt $ontology_directory/yago-ontology.owl
rm $ontology_directory/yago-ontology.nt





