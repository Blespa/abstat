#!/bin/bash

set -e
relative_path=`dirname $0`
root=`cd $relative_path;pwd`
project=$root/../summarization
build_directory=$project/bin
classes_directory=$build_directory/classes

cd $project
rm -rf $classes_directory
mkdir -p $classes_directory
javac -encoding utf8 -cp .:'lib/*' $(find ./* | grep '\.java') -d $classes_directory
cd $classes_directory
for file in $(find ../../lib/* | grep .jar)
do
	jar xf $file
done
jar cvfe ../../ontology_summarization.jar -C . > /dev/null
chmod 777 ../../ontology_summarization.jar


