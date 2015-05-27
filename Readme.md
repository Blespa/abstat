# ABSTAT

## Development requirements

* [docker](https://docs.docker.com/)

Tested on Linux Mint 17 and Mac OS X

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

The summarization process of an arbitrary dataset ```$DATASET``` expects to find an ontology in ```data/datasets/$DATASET/ontology``` and a ntriple file in ```data/datasets/$DATASET/triples/dataset.nt```. First, ensure that ABSTAT is running (if not, issue an ```abstat.sh start```), then:
```
$ abstat.sh run pipeline/run-summarization-pipeline.sh $DATASET
```
The result of the summarization can be found in ```data/summaries/$DATASET```.


## Indexing an RDF Summary

Once the summarization pipeline is run for a dataset ```$DATASET```, you can index the results into the embedded Virtuoso triple store. As for running the pipeline, first ensure that ABSTAT is running, then:
```
$ abstat.sh run pipeline/export-to-rdf.sh $DATASET
```
Where the argument $RESULTS is the directory that contains the results of the analysis from the previous script, $TMP_DIR must point to the directory ```summarization-output``` of the root of the repository and $GRAPH is the iri of the graph that will contain the exported rdf data


## Production use

### Machines

* 10.109.149.57 - behind RICERCA vpn - complete installation
* 193.204.59.21 - bari server - only summarization (no indexing and no webapp)

### Monitoring

The web interface is constantly monitored, since has to be accessible all the time. You can view the current status [here](http://uptime.statuscake.com/?TestID=TCI9iWyOqa)

### Managing the Web interface

To start | stop the web app:

```
#!bash
$ ssh schema-summaries@siti-rack.siti.disco.unimib.it
$ sudo service ld-summaries [start | stop]
```

### Configuring a production machine (ubuntu server)

To configure a production machine do the following steps. First login into the machine using a user that is allowed to run sudo commands and then:

```
#!bash
$ sudo apt-get install python-software-properties
$ sudo add-apt-repository ppa:webupd8team/java
$ sudo apt-get update
$ sudo apt-get install oracle-jdk7-installer git wget bzip2 unzip
$ sudo adduser schema-summaries	# choose an appropriate password
$ sudo visudo 	# append the following lines at the bottom (remeber to cut comments of)
# schema-summaries ALL=(ALL) NOPASSWD: /bin/ln -s /home/schema-summaries/schema-summaries/scripts/java-ui-production.sh /etc/init.d/ld-summaries
# schema-summaries ALL=(ALL) NOPASSWD: /usr/sbin/update-rc.d ld-summaries defaults
# schema-summaries ALL=(ALL) NOPASSWD: /bin/rm -f /etc/init.d/ld-summaries
# schema-summaries ALL=(ALL) NOPASSWD: /usr/sbin/service ld-summaries start
# schema-summaries ALL=(ALL) NOPASSWD: /usr/sbin/service ld-summaries stop
$ su schema-summaries
$ cd
$ git clone https://bitbucket.org/rporrini/schema-summaries.git
$ cd schema-summaries
$ scripts/end2end-test.sh # follow all the hints that the scripts gives to you and re-launch it untill you get no errors
$ git remote set-url origin git@bitbucket.org:rporrini/schema-summaries.git
$ chmod 700 scripts/deploy_rsa
```

### Deployment

#### Summarization Deploy (only summarization)

from your development machine:

```
$ scripts/deploy-summarization.sh USER@HOST
```

#### Full Deploy (webapp, summarization, backend)

from your development machine:

```
$ scripts/deploy-full.sh USER@HOST
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

