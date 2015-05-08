#!/bin/bash

function is_reachable(){
	ping -q -c 1 $1 > /dev/null
}

rdf_export_path=$1

echo "SYSTEM TEST"
echo

echo "checking system configuration"
hosts=(149.132.176.73 193.204.59.21 bitbucket.org)
for i in ${hosts[@]}; do
	if ! is_reachable $i ; then
		echo $i is not reachable
		exit 1
	fi
done

virtuoso_config_file=/etc/virtuoso-opensource-6.1/virtuoso.ini
if ! command -v virtuoso-t ; then
	echo "no virtuoso end point detected"
	echo "installing via apt-get"
	echo 
	echo "\e[0;31m WARNING:\e[0m remember to set up the dba user password equal to 'dba'"	
	echo
	sudo apt-get install virtuoso-opensource virtuoso-server virtuoso-vsp-startpage virtuoso-vad-conductor
	echo
	echo "configuring virtuoso to watch ${rdf_export_path}"
	echo	
	sudo sed -i -e "s|= \., /usr/share/virtuoso-opensource-6.1/vad|= \., /usr/share/virtuoso-opensource-6.1/vad, ${rdf_export_path}|g" $virtuoso_config_file
	sudo service virtuoso-opensource-6.1 force-reload	
fi
if ! [[ $(grep $rdf_export_path $virtuoso_config_file) ]]
then
	echo
	echo "virtuoso is not configured properly:"
	echo "add ${rdf_export_path} to the DirsAllowed parameter in ${virtuoso_config_file}"
	echo
	exit 1
fi
if ! [[ $(curl --silent -i -H "Origin: http://localhost:1234" http://localhost:8890/sparql | grep "Access-Control-Allow-Origin: *") ]]
then
	echo
	echo "virtuoso is not configured for allowing Cross-Origin Resource Sharing over the uri '/sparql'. Please configure it following the tutorial on:"
	echo "http://virtuoso.openlinksw.com/dataspace/doc/dav/wiki/Main/VirtTipsAndTricksGuideCORSSetup#Server-level+CORS+Setup"
	echo
	exit 1
fi
if ! [[ $(curl --silent -i http://localhost:8890/describe/?uri=any | grep 200) ]]
then
	echo
	echo "virtuoso is not configured to provide the url '/describe' for resources loaded in the triplestore. Please configure it following the tutorial on:"
	echo "http://virtuoso.openlinksw.com/dataspace/doc/dav/wiki/Main/VirtFacetBrowserInstallConfig"
	echo "NOTE: download the VAD package from http://opldownload.s3.amazonaws.com/uda/vad-packages/6.4/virtuoso/fct_dav.vad"
	echo
	exit 1
fi

