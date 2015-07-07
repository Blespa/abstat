#!/bin/bash

function is_reachable(){
	ping -q -c 1 $1 > /dev/null
}

echo "checking system configuration"
hosts=(backend bitbucket.org)
for i in ${hosts[@]}; do
	if ! is_reachable $i ; then
		echo $i is not reachable
		exit 1
	fi
done
echo done


