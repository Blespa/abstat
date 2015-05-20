#!/bin/bash

function is_reachable(){
	ping -q -c 1 $1 > /dev/null
}

echo "checking system configuration"
hosts=(149.132.176.73 193.204.59.21 bitbucket.org)
for i in ${hosts[@]}; do
	if ! is_reachable $i ; then
		echo $i is not reachable
		exit 1
	fi
done
echo done


