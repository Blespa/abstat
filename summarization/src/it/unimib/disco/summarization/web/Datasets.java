package it.unimib.disco.summarization.web;


import java.io.File;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

public class Datasets {

	public Datasets(File directory) {
	}

	public InputStream get(RequestParameters request) {
		return IOUtils.toInputStream("{\"datasets\":[]}");
	}
}
