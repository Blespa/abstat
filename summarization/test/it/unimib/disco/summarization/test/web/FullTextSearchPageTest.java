package it.unimib.disco.summarization.test.web;

import static org.hamcrest.Matchers.containsString;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class FullTextSearchPageTest
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
		application.httpAssert().statusOf("search", 200);
	}
	
	@Test
	public void shouldResponseWithTheRightPage() throws Exception{
		application.httpAssert().body("search", containsString("search"));
	}
}
