# Schema Summarization

## System Requirements

* linux (tested on Linux Mint 17 Qiana)
* java
* git
* wget
* bzip2

## Checking out the repository and configuring your local machine
```
#!bash
$ git clone https://bitbucket.org/rporrini/schema-summaries.git
$ cd schema-summaries
$ git checkout development
$ scripts/test-summarization-pipeline.sh
```
If everything goes as expected the script will print "OK".

## Useful scripts

Running the whole summarization pipeline:
```
$ scripts/run-summarization-pipeline.sh $DATA $RESULTS
```
Where the arguments $DATA and $RESULTS are directories. The scripts expects to find an ontology in ```$DATA/ontology``` and a ntriple file in ```$DATA/triples/dataset.nt```

Preparing the dbpedia dataset
```
$ scripts/prepare-dbpedia-dataset.sh $TARGET-DIRECTORY $VERSION
```
Where $VERSION is the dbpedia version that you want to download (e.g., 3.9 or 2014)

Preparing the linked-brainz dataset
```
$ scripts/prepare-linked-brainz-dataset.sh $TARGET-DIRECTORY
```

## Production machine Configuration and Deployment

To configure a production machine do the following steps. First login into the machine, install all the dependencies listed above and then:

```
#!bash
$ cd
$ git clone https://bitbucket.org/rporrini/schema-summaries.git
$ cd schema-summaries
$ git remote set-url origin git@bitbucket.org:rporrini/schema-summaries.git
$ chmod 700 scripts/deploy_rsa
```

To deploy the latest version of the code, from your development machine:

```
$ scripts/deploy.sh USER@HOST
```

