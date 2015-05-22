#! /bin/bash

set -e

ssh -t $1 "export GIT_SSH=~/schema-summaries/deployment/git+ssh.sh && cd ~/schema-summaries && git remote set-url origin git@bitbucket.org:rporrini/schema-summaries.git && chmod 700 deployment/deploy_rsa && git pull && git checkout -f master && ./install.sh $2"

