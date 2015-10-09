package it.unimib.disco.summarization.web;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.server.Request;

public class ServerCommunication implements Communication{

	private HttpServletRequest request;
	private HttpServletResponse response;
	private Request base;

	public ServerCommunication(HttpServletRequest request, HttpServletResponse response, Request base) {
		this.request = request;
		this.response = response;
		this.base = base;
	}

	@Override
	public void setContentType(String contentType) {
		this.response.setContentType(contentType);
	}

	@Override
	public void setOutputStream(InputStream content) throws IOException {
		IOUtils.copy(content, this.response.getOutputStream());
	}

	@Override
	public void setHandled() {
		this.base.setHandled(true);
	}

	@Override
	public String getParameter(String parameter) {
		return this.request.getParameter(parameter);
	}
}
