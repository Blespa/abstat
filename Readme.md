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
$ abstat.sh exec $SCRIPT # runs a script within an already running ABSTAT container.
```
```
$ abstat.sh run $COMMAND # runs an arbitrary bash command on an ABSTAT container.
```

## Running the Summarization Pipeline

The summarization process of a dataset ```$DATASET``` expects to find an ontology in ```data/datasets/$DATASET/ontology``` and a ntriple file in ```data/datasets/$DATASET/triples/dataset.nt```. First, ensure that ABSTAT is running (if not, issue an ```abstat.sh start```), then:
```
$ abstat.sh exec pipeline/run-summarization-pipeline.sh $DATASET
```
The result of the summarization can be found in ```data/summaries/$DATASET```.


## Indexing an RDF Summary

Once the summarization pipeline is run for a dataset ```$DATASET```, you can index the results into the embedded Virtuoso triple store. As for running the pipeline, first ensure that ABSTAT is running, then:
```
$ abstat.sh exec pipeline/export-to-rdf.sh $DATASET
```
