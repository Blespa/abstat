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
	public void sendResponse(Request base, HttpServletResponse response) throws Exception {
		IOUtils.copy(IOUtils.toInputStream(message), response.getOutputStream());
		base.setHandled(true);
	}
}