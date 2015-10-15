package it.unimib.disco.summarization.web;

import javax.servlet.http.HttpServletRequest;

public class HttpParameters implements RequestParameters {

	private HttpServletRequest request;

	public HttpParameters(HttpServletRequest request) {
		this.request = request;
	}

	@Override
	public String get(String name) {
		return this.request.getParameter(name);
	}

}
