package it.unimib.disco.summarization.test.system;

import static org.hamcrest.Matchers.containsString;
import it.unimib.disco.summarization.test.web.HttpAssert;

import org.junit.Test;

public class HttpRoutingTest {

	@Test
	public void theApplicationShouldBeUp() throws Exception {
		
		new HttpAssert("http://localhost").body("/", containsString("ABSTAT"));
	}

	@Test
	public void nonStandardPortShouldBeRedirectToStandardPort() throws Exception {
		new HttpAssert("http://localhost:8880").body("/", containsString("ABSTAT"));
	}
	
	@Test
	public void theSparqlEndpointShouldBeUp() throws Exception {
		new HttpAssert("http://localhost").body("/sparql", containsString("SPARQL"));
	}
	
	@Test
	public void describeCapabilityShouldBeExposed() throws Exception {
		
		String describePerson = "/describe/?uri=http%3A%2F%2Fld-summaries.org%2Fresource%2Fdbpedia-2014%2Fwww.ontologydesignpatterns.org%2Font%2Fdul%2FDUL.owl%23NaturalPerson";
		
		new HttpAssert("http://localhost").body(describePerson, containsString("About:"));
	}
	
	@Test
	public void virtuosoAdminInterfaceShouldBeUp() throws Exception {
		new HttpAssert("http://localhost:8881").body("/conductor", containsString("virtuoso"));
	}
	
	@Test
	public void solrAdminInterfaceShouldBeUp() throws Exception {
		new HttpAssert("http://localhost:8882").body("/solr/", containsString("Solr Admin"));
	}
	
	@Test
	public void solrAdminInterfaceShouldBeLockedUpForStandardUsage() throws Exception {
		
		new HttpAssert("http://localhost").statusOf("/solr", 404);
	}
	
	@Test
	public void downloadRouteShouldBeAccessible() throws Exception {
		
		new HttpAssert("http://localhost").statusOf("downloads", 200);
	}
}
