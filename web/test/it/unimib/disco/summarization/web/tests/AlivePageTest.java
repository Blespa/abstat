package it.unimib.disco.summarization.web.tests;

import it.unimib.disco.summarization.web.SummarizationInspection;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class AlivePageTest {

	SummarizationInspection server = new SummarizationInspection();
	
	@Before
	public void startServer() throws Exception{
		server.on(8888).start();
	}
	
	@After
	public void stopServer() throws Exception{
		server.stop();
	}
	
	@Test
	public void shouldRespond200() throws Exception {
		new HttpAssert("http://localhost:8888").statusOf("/alive", 200);
	}
}
