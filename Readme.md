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
scripts/build-java-summarization-module.sh
```

Running the summarization module:
```
scripts/build-java-summarization-module.sh
```

Running the whole summarization pipeline:
```
$ scripts/run-summarization-pipeline.sh DATA RESULTS
```

Where both DATA and RESULTS are directories. The scripts expects to find an ontology in ```DATA/ontology``` and a ntriple file in ```DATA/triples/dataset.nt```
