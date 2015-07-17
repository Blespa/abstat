package it.unimib.disco.summarization.web;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

public class Application extends AbstractHandler{
	
	@Override
	public void handle(String path, Request base, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		base.setQueryEncoding("utf-8");
		response.setContentType("text/html");
		response.setCharacterEncoding("utf-8");
		request.getSession();
		
		if(path.equals("/alive")){
			base.setHandled(true);
			response.getWriter().write("OK");
		}
		if(path.equals("/")){
			base.setHandled(true);
			IOUtils.copy(FileUtils.openInputStream(new File("views/home.html")), response.getOutputStream());
		}
		if(path.equals("/property-similarity")){
			base.setHandled(true);
			IOUtils.copy(FileUtils.openInputStream(new File("views/property-similarity.html")), response.getOutputStream());
		}
	}
}