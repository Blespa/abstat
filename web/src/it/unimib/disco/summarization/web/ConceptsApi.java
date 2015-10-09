package it.unimib.disco.summarization.web;

import it.unimib.disco.summarization.web.tests.ClientCommunication;

import java.io.InputStream;
import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;

public class ConceptsApi implements Api {
	
	private final String path = "/solr/indexing/concept-suggest";
	private final String host = "http://localhost:8891";

	@Override
	public InputStream getAutocomplete(Communication communication) throws Exception {
		return new ClientCommunication(host).httpGet(path + queryString(communication)).getEntity().getContent();
	}

	private String queryString(Communication communication) {
		ArrayList<String> parameters = new ArrayList<String>();
		parameters.add(queryParameter("q", "URI_ngram", communication.getParameter("q")));
		parameters.add(queryParameter("fq", "dataset", communication.getParameter("dataset")));
		return "?" + StringUtils.join(parameters, "&");
	}

	private String queryParameter(String queryParameter, String solrParameter, String solrValue) {
		return queryParameter + "=" + solrParameter + ":" + solrValue;
	}

}
