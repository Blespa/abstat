package it.unimib.disco.summarization.web;

import it.unimib.disco.summarization.export.Events;

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
			String currentVersion = version.branch() + "-" + version.commit();
			
			new Routing()
				.mapText("/alive", "OK")
				.mapText("/version", currentVersion)
				.mapFile("/", "home.html")
				.mapFile("/search", "search.html")
				.mapFile("/experiment/browse", "experiment.html")
				.mapFile("/experiment/search", "search.html")
				.mapFile("/property-similarity", "property-similarity.html")
				.mapJson("/api/v1/autocomplete/concepts", new SolrAutocomplete(new SolrConnector(), "concept-suggest"))
				.mapJson("/api/v1/autocomplete/properties", new SolrAutocomplete(new SolrConnector(), "property-suggest"))
				.routeTo(path)
			.sendTo(base, response, new HttpParameters(request));
		}catch(Exception e){
			Events.web().error("processing request: " + path, e);
			response.setStatus(500);
		}
	}
}
