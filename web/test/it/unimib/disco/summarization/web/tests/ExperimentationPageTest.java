package it.unimib.disco.summarization.web.tests;

import static org.hamcrest.Matchers.containsString;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ExperimentationPageTest
{
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
	public void shouldBeAccessible() throws Exception{
		application.httpAssert().statusOf("experimentation", 200);
	}
	
	@Test
	public void shouldResponseWithTheRightPage() throws Exception{
		application.httpAssert().body("experimentation", containsString("search"));
	}
}