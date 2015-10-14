package it.unimib.disco.summarization.web;

import java.io.InputStream;

public class PropertiesApi implements Api {

	private Connector connector;

	public PropertiesApi(Connector connector) {
		this.connector = connector;
	}

	@Override
	public InputStream getResponseFromConnector(RequestParameters request) throws Exception {
		QueryString queryString = new QueryString()
										.addParameter("q", "URI_ngram", request.getParameter("q"))
										.addParameter("fq", "dataset", request.getParameter("dataset"));

		return this.connector.query("/solr/indexing/property-suggest", queryString);
	}

}
