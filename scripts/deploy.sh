#! /bin/bash

set -e

ssh -t $1 "export GIT_SSH=~/schema-summaries/scripts/git+ssh.sh && cd ~/schema-summaries && git checkout master && git pull && scripts/test-summarization-pipeline.sh && scripts/isql.sh 'SPARQL CLEAR GRAPH <http://ld-summaries.org/system-test>;' && scripts/build-java-ui-module.sh && sudo rm -f /etc/init.d/ld-summaries && sudo ln -s /home/schema-summaries/schema-summaries/scripts/java-ui-production.sh /etc/init.d/ld-summaries && sudo update-rc.d ld-summaries defaults && sudo service ld-summaries stop && sudo service ld-summaries start"

