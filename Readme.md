# ABSTAT

## Prerequisites for running and developing ABSTAT

* [docker](https://docs.docker.com/), install from [here](https://docs.docker.com/installation/)

Tested on Linux Mint 17, Mac OS X, Ubuntu 14.04

## Checking out the repository and configuring your local machine
```
#!bash
$ git clone https://bitbucket.org/rporrini/schema-summaries.git
$ cd schema-summaries
$ git checkout development
$ ./build-and-test.sh
```

## Controlling ABSTAT

ABSTAT can be controlled using the script ```abstat.sh``` from the root of the repository with the following commands:
```
$ abstat.sh build # builds ABSTAT and the respective docker container
```
```
$ abstat.sh start # starts ABSTAT
```
```
$ abstat.sh destroy # stops ABSTAT and deletes all the respective docker container
```
```
$ abstat.sh status # prints out the current status of ABSTAT. Useful to see if ABSTAT is running or not
```
```
$ abstat.sh log # prints out all the available logging information for ABSTAT. Useful to see if ABSTAT is running or not
```
```
$ abstat.sh run $SCRIPT # runs the script within the ABSTAT container.
```

## Running the Summarization Pipeline

The summarization process of a dataset ```$DATASET``` expects to find an ontology in ```data/datasets/$DATASET/ontology``` and a ntriple file in ```data/datasets/$DATASET/triples/dataset.nt```. First, ensure that ABSTAT is running (if not, issue an ```abstat.sh start```), then:
```
$ abstat.sh run pipeline/run-summarization-pipeline.sh $DATASET
```
The result of the summarization can be found in ```data/summaries/$DATASET```.


## Indexing an RDF Summary

Once the summarization pipeline is run for a dataset ```$DATASET```, you can index the results into the embedded Virtuoso triple store. As for running the pipeline, first ensure that ABSTAT is running, then:
```
$ abstat.sh run pipeline/export-to-rdf.sh $DATASET
```
Where the argument $RESULTS is the directory that contains the results of the analysis from the previous script, $TMP_DIR must point to the directory ```summarization-output``` of the root of the repository and $GRAPH is the iri of the graph that will contain the exported rdf data.

## Production

### Machines

* 10.109.149.57 - ```abstatweb01``` - behind RICERCA vpn - complete installation
* 193.204.59.21 - bari server - summarization only

### Monitoring

The web interface is constantly monitored, since has to be accessible all the time. You can view the current status [here](http://uptime.statuscake.com/?TestID=TCI9iWyOqa)

### Managing ABSTAT in production

To start | stop ABSTAT:

```
#!bash
$ ssh schema-summaries@siti-rack.siti.disco.unimib.it
$ cd schema-summaries
$ ./abstat.sh start | destroy
```

### Configuring a production machine (Ubuntu server 14.04 LTS)

First login into the machine using a user that is allowed to run sudo commands and then:

```
#!bash
$ sudo apt-get update
$ sudo apt-get install wget
$ wget -qO- https://get.docker.com/ | sh
$ sudo adduser schema-summaries	# choose an appropriate password
$ sudo usermod -aG docker schema-summaries
$ su schema-summaries
$ cd
$ git clone https://bitbucket.org/rporrini/schema-summaries.git
$ git checkout master
$ git pull
$ git remote set-url origin git@bitbucket.org:rporrini/schema-summaries.git
$ chmod 700 scripts/deploy_rsa
```

### Deployment

#### Backend

```
$ deployment/deploy.sh USER@HOST --backend
```

#### Webapp

```
$ deployment/deploy.sh USER@HOST
```

### Full
```
$ deployment/deploy-all.sh USER@HOST
```

### VPN configuration

```
[vpn]
service-type=org.freedesktop.NetworkManager.pptp
password-flags=1
require-mppe-128=yes
mppe-stateful=yes
user= ######
refuse-eap=yes
refuse-chap=yes
gateway=prx14-in-u7.servizi.didattica.unimib.it
domain=RICERCA
refuse-pap=yes

[ipv4]
method=auto
dns=10.109.149.38;
dns-search=ricerca.didattica.unimib.it;
route1=10.109.149.57/32,149.132.157.130,0
never-default=true
```

