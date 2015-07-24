package it.unimib.disco.summarization.web;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
		
		try{
			DeployedVersion version = new DeployedVersion(new File(".."));
			String message = version.branch() + "-" + version.commit();
			
			Response r = new Routing()
								.mapFile("/", "home.html")
								.mapFile("/property-similarity", "property-similarity.html")
								.mapText("/alive", "OK")
								.mapText("/version", message)
								.routeTo(path);
			IOUtils.copy(r.stream(), response.getOutputStream());
			base.setHandled(true);
		}catch(Exception e){
			new Events().error("processing request: " + path, e);
			response.setStatus(500);
		}
	}
}