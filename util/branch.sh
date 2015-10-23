#!/bin/bash

action=$1

set -e
relative_path=`dirname $0`
root=`cd $relative_path;pwd`
cd $root/..

if [ $(git status -s | wc -l) != 0 ] 
then
	echo 'aborting due to local modifications'
	git status
	exit 1
fi

git fetch origin

case $action in
	new )
		source_branch=$2
		new_branch=$3
		git checkout $source_branch
		git checkout -b $new_branch
		git push --set-upstream origin $new_branch
		;;
	promote )
		git checkout development
		git pull
		git checkout master
		git pull
		git merge development
		git push
		;;
	* )
		echo "usage: branch.sh new original-branch new-branch | promote"
		echo "new = creates a new branch"
		echo "promote = pushes the current development branch to the master branch"
	;;
esac
