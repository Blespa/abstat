#!/bin/bash

set -e
relative_path=`dirname $0`
root=`cd $relative_path;pwd`
project=$root/../web
build_directory=$project/bin
classes_directory=$build_directory/classes

echo building the summarization module

cd $project
rm -rf $classes_directory
mkdir -p $classes_directory
javac -encoding utf8 -cp .:'lib/*' $(find ./* | grep '\.java') -d $classes_directory
cd $classes_directory
for file in $(find ../../lib/* | grep .jar)
do
	jar xf $file
done
jar cvfe ../../summarization-web.jar -C . > /dev/null
chmod 777 ../../summarization-web.jar

echo done


