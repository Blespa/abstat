package it.unimib.disco.summarization.web.tests;

import java.util.HashMap;

import it.unimib.disco.summarization.web.RequestParameters;

public class RequestTestDouble implements RequestParameters {

	private HashMap<String, String> parameters;

	public RequestTestDouble() {
		this.parameters = new HashMap<String, String>();
	}
	
	@Override
	public String getParameter(String name) {
		return parameters.get(name);
	}

	public RequestTestDouble withParameter(String name, String value) {
		parameters.put(name, value);
		return this;
	}

}
