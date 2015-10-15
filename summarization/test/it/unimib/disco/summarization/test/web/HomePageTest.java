package it.unimib.disco.summarization.test.web;

import static org.hamcrest.Matchers.containsString;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class HomePageTest {

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
	public void shouldRespond200() throws Exception {
		application.httpAssert().statusOf("/", 200);
	}
	
	@Test
	public void shouldContainTheTitle() throws Exception {
		application.httpAssert().body("/", containsString("linked data summaries"));
	}
}
