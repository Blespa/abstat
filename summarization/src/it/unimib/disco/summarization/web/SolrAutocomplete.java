package it.unimib.disco.summarization.web;

import java.io.InputStream;

public class SolrAutocomplete implements Api{
	
	private Connector connector;
	private String suggestionService;

	public SolrAutocomplete(Connector connector, String service) {
		this.connector = connector;
		this.suggestionService = service;
	}

	@Override
	public InputStream get(RequestParameters request) throws Exception {
		QueryString queryString = new QueryString()
									.addParameter("q", "URI_ngram", request.get("q"))
									.addParameter("fq", "dataset", request.get("dataset"));
		
		return connector.query("/solr/indexing/" + suggestionService, queryString);
	}
}
