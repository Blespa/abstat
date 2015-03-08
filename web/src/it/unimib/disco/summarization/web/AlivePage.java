package it.unimib.disco.summarization.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

class AlivePage extends AbstractHandler{
	
	@Override
	public void handle(String path, Request base, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		base.setQueryEncoding("utf-8");
		response.setContentType("text/html");
		response.setCharacterEncoding("utf-8");
		base.setHandled(true);
	}
}