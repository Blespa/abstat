#!/bin/bash

set -e
isql-vt 1111 dba dba VERBOSE=OFF EXEC="$@"
