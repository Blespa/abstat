package it.unimib.disco.summarization.test.web;

import it.unimib.disco.summarization.web.Connector;
import it.unimib.disco.summarization.web.QueryString;

import java.io.InputStream;

import org.apache.commons.io.IOUtils;

public class SolrConnectorTestDouble implements Connector {

	String lastRequest = "";
	
	@Override
	public InputStream query(String path, QueryString queryString) throws Exception {
		lastRequest = path + queryString.build();
		return IOUtils.toInputStream("");
	}

	public String requestedUrl() {
		return lastRequest;
	}
}
