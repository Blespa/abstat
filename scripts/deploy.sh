#! /bin/bash

set -e

ssh $1 "export GIT_SSH=~/schema-summaries/scripts/git+ssh.sh && cd ~/schema-summaries && git checkout master && git pull && scripts/test-annotation-pipeline.sh"

