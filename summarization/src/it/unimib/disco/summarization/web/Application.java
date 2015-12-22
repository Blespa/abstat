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
			Routing routes = new Routing();
			
			serviceAPI(routes);
			mainUI(routes);
			API(routes);
			experiment(routes);
			experimentalFeatures(routes);
			
			routes.routeTo(path).sendTo(base, response, new HttpParameters(request));
			
		}catch(Exception e){
			Events.web().error("processing request: " + path, e);
			response.setStatus(500);
		}
	}

	private void experimentalFeatures(Routing routes) {
		routes.mapFile("/property-similarity", "property-similarity.html");
	}

	private void API(Routing routes) {
		routes
			.mapJson("/api/v1/autocomplete/concepts", new SolrAutocomplete(new SolrConnector(), "concept-suggest"))
			.mapJson("/api/v1/autocomplete/properties", new SolrAutocomplete(new SolrConnector(), "property-suggest"))
			.mapJson("/api/v1/datasets", new Datasets(new File("../data/summaries")));
	}

	private void experiment(Routing routes) {
		routes
			.mapFile("/experiment/browse", "experiment-browse.html")
			.mapFile("/experiment/search", "experiment-search.html")
			.mapFile("/experiment/query", "experiment-sparql.html");
	}

	private void mainUI(Routing routes) {
		routes
			.mapFile("/", "browse.html")
			.mapFile("/search", "search.html")
			.mapFile("/about", "about.html");
	}

	private void serviceAPI(Routing routes) throws Exception {
		DeployedVersion version = new DeployedVersion(new File(".."));
		String currentVersion = version.branch() + "-" + version.commit();
		routes.mapText("/alive", "OK")
			  .mapText("/version", currentVersion);
	}
}
