package it.unimib.disco.summarization.web;

import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;

public interface Response {

	public void sendTo(Request base, HttpServletResponse response, RequestParameters request) throws Exception;
}