package it.unimib.disco.summarization.test.web;

import java.util.HashMap;

import it.unimib.disco.summarization.web.RequestParameters;

public class RequestTestDouble implements RequestParameters {

	private HashMap<String, String> parameters;

	public RequestTestDouble() {
		this.parameters = new HashMap<String, String>();
	}
	
	@Override
	public String get(String name) {
		return parameters.get(name);
	}

	public RequestTestDouble withParameter(String name, String value) {
		parameters.put(name, value);
		return this;
	}

}
