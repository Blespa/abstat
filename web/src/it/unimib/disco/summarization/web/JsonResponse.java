package it.unimib.disco.summarization.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.server.Request;

public class JsonResponse implements Response {

	public JsonResponse(HttpServletRequest request) {
	}

	@Override
	public void sendResponse(Request base, HttpServletResponse response) throws Exception {
		response.setContentType("application/json");
		IOUtils.copy(IOUtils.toInputStream("work in progress"), response.getOutputStream());
		base.setHandled(true);
	}

}
