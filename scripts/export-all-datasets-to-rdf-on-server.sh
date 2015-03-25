#!/bin/bash

set -e

ssh $1 "~/schema-summaries/scripts/export-to-rdf.sh dbpedia-2014-results schema-summaries/summarization-output http://ld-summaries.org/dbpedia-2014 && ~/schema-summaries/scripts/export-to-rdf.sh linked-brainz-results schema-summaries/summarization-output http://ld-summaries.org/linked-brainz"
