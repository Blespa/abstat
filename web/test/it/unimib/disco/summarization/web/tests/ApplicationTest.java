package it.unimib.disco.summarization.web.tests;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ApplicationTest {

	private SummarizationTestApplication application = new SummarizationTestApplication();

	@Before
	public void setUp() throws Exception{
		application.start();
	}
	
	@After
	public void tearDown() throws Exception{
		application.stop();
	}
	
	@Test
	public void shouldTrackTheSessions() throws Exception {
		
		application.httpAssert().cookie("/", "JSESSIONID");
	}
}
