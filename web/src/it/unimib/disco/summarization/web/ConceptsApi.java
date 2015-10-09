package it.unimib.disco.summarization.web;

import java.io.InputStream;

public class ConceptsApi {
	
	private Connector connector;

	public ConceptsApi(Connector solrConnector) {
		this.connector = solrConnector;
	}

	public InputStream getResponseFromConnector(RequestParameters request) throws Exception {
		QueryString queryString = new QueryString()
									.addParameter("q", "URI_ngram", request.getParameter("q"))
									.addParameter("fq", "dataset", request.getParameter("dataset"));
		
		return connector.query("/solr/indexing/concept-suggest", queryString);
	}
}
