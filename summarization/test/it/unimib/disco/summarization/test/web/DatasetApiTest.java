package it.unimib.disco.summarization.test.web;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DatasetApiTest {

	SummarizationTestApplication application = new SummarizationTestApplication();
	
	@Before
	public void startServer() throws Exception{
		application.start();
	}
	
	@After
	public void stopServer() throws Exception{
		application.stop();
	}
	
	@Test
	public void shouldAnswer() throws Exception {
		
		application.httpAssert().statusOf("/api/v1/datasets", 200);
	}

}
