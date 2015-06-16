#!/bin/bash

set -e

directory=$1
jar=$2

project=../$directory
build_directory=$project/bin
classes_directory=$build_directory/classes

echo building the content of $directory to $jar
cd $project
rm -rf $classes_directory
mkdir -p $classes_directory
javac -encoding utf8 -cp .:'lib/*' $(find ./* | grep '\.java') -d $classes_directory
cd $classes_directory
for file in $(find ../../lib/* | grep .jar)
do
	jar xf $file
done
jar cvfe ../../$jar -C . > /dev/null
chmod 777 ../../$jar
echo done
