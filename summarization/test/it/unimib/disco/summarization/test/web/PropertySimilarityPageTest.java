package it.unimib.disco.summarization.test.web;

import static org.hamcrest.Matchers.containsString;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PropertySimilarityPageTest {
	
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
	public void shouldBeAccessible() throws Exception {
		
		application.httpAssert().statusOf("property-similarity", 200);
	}
	
	@Test
	public void shouldRespondWithTheRightPage() throws Exception {
		
		application.httpAssert().body("property-similarity", containsString("property similarity"));
	}
}
