package it.unimib.disco.summarization.web;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.server.Request;



public class JsonResponse implements Response {

	private ConceptsApi api;

	public JsonResponse(ConceptsApi api) {
		this.api = api;
	}

	@Override
	public void sendResponse(Request base, HttpServletResponse response, RequestParameters parameters) throws Exception {
		response.setContentType("application/json");
		response.addHeader("Access-Control-Allow-Origin", "*");
		IOUtils.copy(this.api.getResponseFromConnector(parameters), response.getOutputStream());
		base.setHandled(true);
	}

}
