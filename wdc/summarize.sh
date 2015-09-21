#!/bin/bash

function as_absolute(){
	echo `cd $1; pwd`
}

set -e
relative_path=`dirname $0`
current_directory=`cd $relative_path;pwd`

filename=$1
dataset=$(echo $filename | cut -d'_' -f2 | cut -d'.' -f1);

cd $current_directory

target_directory=../data/datasets/$dataset

rm -rf $target_directory
mkdir -p $target_directory
mkdir -p $target_directory/ontology $target_directory/triples

cp sdo.owl $target_directory/ontology

rdfconvert-0.3.2/bin/rdfconvert.sh -i 'N-Quads' -o 'N-Triples' $filename $target_directory/triples/$dataset.nt
java -jar skolem.jar LabelRDFGraph -i $target_directory/triples/$dataset.nt -s 3 -p http://skolem.com/.well-known/genid/ -o $target_directory/triples/dataset.nt
rm $target_directory/triples/$dataset.nt 

cd ..

./abstat.sh exec pipeline/run-summarization-pipeline.sh $dataset
./abstat.sh exec pipeline/export-to-rdf.sh $dataset


