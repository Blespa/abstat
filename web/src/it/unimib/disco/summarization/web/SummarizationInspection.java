package it.unimib.disco.summarization.web;

import org.eclipse.jetty.server.Server;

public class SummarizationInspection {

	private Server server;

	public SummarizationInspection on(int port) {
		new Events();
		server = new Server(port);
		server.setHandler(new AlivePage());
		return this;
	}

	public SummarizationInspection start() throws Exception {
		server.start();
		return this;
	}

	public SummarizationInspection stop() throws Exception {
		server.stop();
		return this;
	}
}
