#!/bin/sh

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

git pull

case $action in
	new )
		source_branch=$2
		new_branch=$3
		git checkout $source_branch
		git checkout -b $new_branch
		git push --set-upstream origin $new_branch
		;;
	delete )
		branch=$2
		git branch -D $branch
		git push origin --delete $branch
		;;
	merge )
		target_branch=$2
		source_branch=$3
		git checkout $target_branch
		git pull
		git merge --no-ff $source_branch
		git push
		;;
esac
