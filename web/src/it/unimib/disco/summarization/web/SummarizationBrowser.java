package it.unimib.disco.summarization.web;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;

public class SummarizationBrowser {

	private Server server;

	public SummarizationBrowser on(int port) throws Exception {
		new Events();
		server = new Server(port);
		HandlerList handlers = new HandlerList();
		handlers.setHandlers(new Handler[]{staticResources(), new Application()});
		server.setHandler(handlers);
		return this;
	}

	public SummarizationBrowser start() throws Exception {
		server.start();
		return this;
	}

	public SummarizationBrowser stop() throws Exception {
		server.stop();
		return this;
	}
	
	private ContextHandler staticResources() throws Exception {
		ResourceHandler resources = new ResourceHandler();
		resources.setDirectoriesListed(false);
		resources.setResourceBase("assets");
		ContextHandler contextHandler = new ContextHandler("/static");
		contextHandler.setHandler(resources);
		return contextHandler;
	}
}
