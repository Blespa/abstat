package it.unimib.disco.summarization.web;


import it.unimib.disco.summarization.ontology.LDSummariesVocabulary;

import java.io.File;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

public class Datasets {

	private Folders folder;

	public Datasets(File directory) {
		this.folder = new Folders(directory);
	}

	public InputStream get(RequestParameters request) {
		String result = "{\"datasets\":[";
		for(String dataset : folder.children()){
			result+="{\"URI\":\"" + new LDSummariesVocabulary(null, dataset).graph() + "\"}";
		}
		result+="]}";
		return IOUtils.toInputStream(result);
	}
}
