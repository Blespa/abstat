#! /bin/bash

set -e

ssh -t $1 "export GIT_SSH=/schema-summaries/deployment/git+ssh.sh && cd /schema-summaries && git checkout master && git pull && ./abstat.sh build && ./abstat.sh destroy && ./abstat.sh start"

