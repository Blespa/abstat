package it.unimib.disco.summarization.web;

import org.eclipse.jetty.server.Server;

public class SummarizationBrowser {

	private Server server;

	public SummarizationBrowser on(int port) {
		new Events();
		server = new Server(port);
		server.setHandler(new Application());
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
}
