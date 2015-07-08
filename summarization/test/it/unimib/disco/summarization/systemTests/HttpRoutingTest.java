package it.unimib.disco.summarization.systemTests;

import static org.hamcrest.Matchers.containsString;

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
	public void solrShouldBeUp() throws Exception {
		new HttpAssert("http://localhost").body("/solr/", containsString("Solr Admin"));
	}
}
