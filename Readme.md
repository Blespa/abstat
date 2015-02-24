# Schema Summarization

## System Requirements

* linux (tested on Linux Mint 17 Qiana)
* git
* java

## Checking out the repository and configuring your local machine
```
#!bash
$ git clone https://bitbucket.org/rporrini/schema-summaries.git
$ cd schema-summaries
$ scripts/test-summarization-pipeline.sh
```

Now open the file located in ```summarization/log/log.txt```. If everything went fine the last lines should be something like

```
End: Merge Data

Total Time: 62 secs
```

## Useful scripts

Building the java summarization module:
```
$ scripts/build-java-summarization-module.sh
```

Running the summarization module:
```
$ scripts/build-java-summarization-module.sh
```

Running the whole summarization pipeline:
```
$ scripts/run-summarization-pipeline.sh DATA RESULTS
```

Where both DATA and RESULTS are directories. The scripts expects to find an ontology in ```DATA/ontology``` and a ntriple file in ```DATA/triples/dataset.nt```

## Production machine Configuration and Deployment

To configure a production machine do the following steps. First login into the machine, install all the dependencies listed above and then:

```
#!bash
$ cd
$ git clone https://bitbucket.org/rporrini/schema-summaries.git
$ git remote set-url origin git@bitbucket.org:rporrini/schema-summaries.git
$ chmod 700 scripts/deploy_rsa
```

To deploy the latest version of the code, from your development machine:

```
$ scripts/deploy.sh USER@HOST
```

