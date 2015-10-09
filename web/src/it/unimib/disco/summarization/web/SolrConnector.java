package it.unimib.disco.summarization.web;

import it.unimib.disco.summarization.web.tests.ClientCommunication;

import java.io.InputStream;

public class SolrConnector implements Connector{
	
	@Override
	public InputStream query(String path, QueryString queryString) throws Exception {
		return new ClientCommunication("http://localhost:8891").httpGet(path + queryString.build()).getEntity().getContent();
	}
}
