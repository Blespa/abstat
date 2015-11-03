package it.unimib.disco.summarization.web;


import it.unimib.disco.summarization.ontology.LDSummariesVocabulary;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

public class Datasets implements Api {

	private Folders folder;

	public Datasets(File directory) {
		this.folder = new Folders(directory);
	}

	public InputStream get(RequestParameters request) {
		ArrayList<String> datasets = new ArrayList<String>();
		for(String dataset : folder.children()){
			datasets.add("{\"URI\":\"" + new LDSummariesVocabulary(null, dataset).graph() + "\"}");
		}
		return IOUtils.toInputStream("{\"datasets\":[" + StringUtils.join(datasets, ",") + "]}");
	}
}
