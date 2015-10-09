package it.unimib.disco.summarization.web;

import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;

public class QueryString {

	private ArrayList<String> parameters;

	public QueryString() {
		this.parameters = new ArrayList<String>();
	}
	
	public QueryString addParameter(String queryParameter, String solrParameter, String solrValue) {
		parameters.add(queryParameter + "=" + solrParameter + ":" + solrValue);
		return this;
	}

	public String build() {
		return "?" + StringUtils.join(parameters, "&");
	}

}
