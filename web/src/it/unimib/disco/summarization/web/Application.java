package it.unimib.disco.summarization.web;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
			String description = version.branch() + "-" + version.commit();
			
			new Routing()
				.mapFile("/", "home.html")
				.mapFile("/property-similarity", "property-similarity.html")
				.mapFile("/search", "search.html")
				.mapFile("/experimentation", "experimentation.html")
				.mapText("/alive", "OK")
				.mapText("/version", description)
				.routeTo(path)
			.sendResponse(base, response);
		}catch(Exception e){
			new Events().error("processing request: " + path, e);
			response.setStatus(500);
		}
	}
}
