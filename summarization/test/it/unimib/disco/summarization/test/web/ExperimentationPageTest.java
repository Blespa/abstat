package it.unimib.disco.summarization.test.web;

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
	public void browsingInterfaceShouldBeAccessible() throws Exception{
		application.httpAssert().statusOf("experiment/browse", 200);
	}
	
	@Test
	public void searchInterfaceShouldBeAccessible() throws Exception{
		application.httpAssert().statusOf("experiment/search", 200);
	}
	
	@Test
	public void searchSparqlShouldBeAccessible() throws Exception{
		application.httpAssert().statusOf("experiment/query", 200);
	}
	
	@Test
	public void browsingInterfaceShouldRespondWithTheRightPage() throws Exception{
		application.httpAssert().body("experiment/browse", containsString("get more"));
	}
	
	@Test
	public void searchInterfaceShouldRespondWithTheRightPage() throws Exception{
		application.httpAssert().body("experiment/search", containsString("search"));
	}
	
	@Test
	public void sparqlInterfaceShouldBeAYASGUIInstance() throws Exception{
		application.httpAssert().body("experiment/query", containsString("yasgui.min.js"));
	}
}