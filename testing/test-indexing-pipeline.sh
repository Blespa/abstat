function as_absolute(){
	echo `cd $1; pwd`
}

set -e
relative_path=`dirname $0`
current_directory=$(as_absolute $relative_path)

cd $current_directory
cd ../pipeline

./identify-internal-resources.sh system-test dbpedia.org
./export-to-solr.sh system-test

cd ../testing
