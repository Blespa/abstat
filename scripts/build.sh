#!/bin/bash

set -e
relative_path=`dirname $0`
root=`cd $relative_path;pwd`
project=$root/../summarization
build_directory=$project/build/classes

cd $project
rm -rf $build_directory
mkdir -p $build_directory
javac -encoding utf8 -cp .:'lib/*' $(find ./* | grep '\.java') -d $build_directory
cd $build_directory
for file in $(find ../../lib/* | grep .jar)
do
	jar xf $file
done
jar cvfe ../../java-bin/ontology_summarization.jar -C . 
chmod 777 ../../java-bin/ontology_summarization.jar

