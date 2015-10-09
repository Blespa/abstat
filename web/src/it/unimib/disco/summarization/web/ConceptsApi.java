package it.unimib.disco.summarization.web;

import it.unimib.disco.summarization.web.tests.ClientCommunication;

import java.io.InputStream;
import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;

public class ConceptsApi implements Api {
	
	private final String path = "/solr/indexing/select";

	@Override
	public InputStream getAutocomplete(Communication communication) throws Exception {
		return new ClientCommunication("http://localhost").httpGet(path + queryString(communication)).getEntity().getContent();
	}

	private String queryString(Communication communication) {
		ArrayList<String> parameters = new ArrayList<String>();
		parameters.add(queryParameter("q", "URI", communication.getParameter("q")));
		parameters.add(queryParameter("fq", "dataset", communication.getParameter("dataset")));
		parameters.add(queryParameter("fq", "type", "concept"));
		parameters.add(solrExtraParameter("wt", "json"));
		parameters.add(solrExtraParameter("fl", "URI"));
		parameters.add(solrExtraParameter("indent", "true"));
		return "?" + StringUtils.join(parameters, "&");
	}

	private String solrExtraParameter(String name, String value) {
		return name + "=" + value;
	}

	private String queryParameter(String queryParameter, String solrParameter, String solrValue) {
		return queryParameter + "=" + solrParameter + ":" + solrValue;
	}

}
