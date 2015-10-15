#!/bin/bash

set -e

relative_path=`dirname $0`
root=`cd $relative_path;pwd`

cd $root

jar=summarization.jar
project=summarization

build_directory=bin
classes_directory=$build_directory/classes

echo "Building the content of $directory to $jar"

cd $project
rm -rf $classes_directory
mkdir -p $classes_directory
javac -encoding utf8 -cp .:'lib/*' $(find ./* | grep '\.java') -d $classes_directory
cd $classes_directory
for file in $(find ../../lib/* | grep .jar)
do
	jar xf $file
done
rm -rf META-INF
jar cvfe ../../$jar -C . > /dev/null
chmod 777 ../../$jar
echo "Done"
