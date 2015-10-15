package it.unimib.disco.summarization.web;

import java.io.InputStream;

public class ConceptsApi implements Api{
	
	private Connector connector;
	private String suggestionService;

	public ConceptsApi(Connector connector, String service) {
		this.connector = connector;
		this.suggestionService = service;
	}

	@Override
	public InputStream get(RequestParameters request) throws Exception {
		QueryString queryString = new QueryString()
									.addParameter("q", "URI_ngram", request.getParameter("q"))
									.addParameter("fq", "dataset", request.getParameter("dataset"));
		
		return connector.query("/solr/indexing/" + suggestionService, queryString);
	}
}
