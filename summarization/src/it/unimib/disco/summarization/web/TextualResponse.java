package it.unimib.disco.summarization.web;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.server.Request;


public class TextualResponse implements Response{
	
	private String message;
	
	public TextualResponse(String message) {
		this.message = message;
	}

	@Override
	public void sendTo(Request base, HttpServletResponse response, RequestParameters request) throws Exception {
		IOUtils.copy(IOUtils.toInputStream(message), response.getOutputStream());
		base.setHandled(true);
	}
}