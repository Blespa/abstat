#! /bin/bash

set -e

ssh $1 "export GIT_SSH=~/schema-summaries/scripts/git+ssh.sh && cd ~/schema-summaries && git checkout master && git pull && scripts/test-summarization-pipeline.sh && scripts/build-java-ui-module.sh && scripts/java-ui.sh stop && scripts/java-ui.sh start 8880"

