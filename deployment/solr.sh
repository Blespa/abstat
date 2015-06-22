#!/bin/bash

set -e
command=$1
port=$2

/schema-summaries/solr/bin/solr $command -p $port

