#!/bin/bash

set -e
relative_path=`dirname $0`
root=`cd $relative_path;pwd`
project=$root/../summarization

cd $project

echo "*************** Exporting Vectors ***************"
java -Xms256m -Xmx16g -cp .:'ontology_summarization.jar' it.unimib.disco.summarization.experiments.ExportPropertyDomainVectors $@
echo "*************** done ***************"

