package it.unimib.disco.summarization.test.web;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class VersionPageTest {

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
	public void shouldRespond200() throws Exception {
		application.httpAssert().statusOf("version", 200);
	}
}
