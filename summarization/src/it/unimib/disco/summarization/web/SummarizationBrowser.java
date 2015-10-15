package it.unimib.disco.summarization.web;

import it.unimib.disco.summarization.export.Events;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.session.HashSessionManager;
import org.eclipse.jetty.server.session.SessionHandler;

public class SummarizationBrowser {

	private Server server;

	public SummarizationBrowser on(int port) throws Exception {
		Events.web();
		server = new Server(port);
		HandlerList handlers = new HandlerList();
		handlers.setHandlers(new Handler[]{trackSessions(), staticResources(), new Application()});
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
	
	private SessionHandler trackSessions() {
		HashSessionManager sessions = new HashSessionManager();
		sessions.setMaxInactiveInterval(60 * 60);
		return new SessionHandler(sessions);
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
