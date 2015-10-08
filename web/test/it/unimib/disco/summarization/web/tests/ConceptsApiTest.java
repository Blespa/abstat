package it.unimib.disco.summarization.web.tests;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.hamcrest.Matchers.containsString;

public class ConceptsApiTest {

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
	public void jsonAsContentType() throws Exception {
		application.httpAssert().contentTypeOf("/api/v1/autocomplete/concepts", containsString("application/json"));
	}
	
	@Test
	public void conceptsApiAccessible() throws Exception {
		application.httpAssert().statusOf("/api/v1/autocomplete/concepts", 200);
	}
}
