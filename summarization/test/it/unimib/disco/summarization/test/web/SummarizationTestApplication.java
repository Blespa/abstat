package it.unimib.disco.summarization.test.web;

import it.unimib.disco.summarization.web.SummarizationBrowser;

public class SummarizationTestApplication{
	
	SummarizationBrowser server = new SummarizationBrowser();
	int port = 8888;
	
	public SummarizationTestApplication start() throws Exception{
		server.on(port).start();
		return this;
	}
	
	public SummarizationTestApplication stop() throws Exception{
		server.stop();
		return this;
	}
	
	public HttpAssert httpAssert(){
		return new HttpAssert("http://localhost:" + port);
	}
}