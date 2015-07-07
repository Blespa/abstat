package it.unimib.disco.summarization.systemTests;

import static org.hamcrest.Matchers.containsString;

import org.junit.Test;

public class HttpRoutingTest {

	@Test
	public void theApplicationShouldBeUp() throws Exception {
		
		HttpAssert.body("http://localhost", containsString("ABSTAT"));
	}

	@Test
	public void nonStandardPortShouldBeRedirectToStandardPort() throws Exception {
		HttpAssert.body("http://localhost:8880", containsString("ABSTAT"));
	}
	
	@Test
	public void theSparqlEndpointShouldBeUp() throws Exception {
		HttpAssert.body("http://localhost/sparql", containsString("SPARQL"));
	}
	
	@Test
	public void describeCapabilityShouldBeExposed() throws Exception {
		
		String describePerson = "http://localhost/describe/?uri=http%3A%2F%2Fld-summaries.org%2Fresource%2Fdbpedia-2014%2Fwww.ontologydesignpatterns.org%2Font%2Fdul%2FDUL.owl%23NaturalPerson";
		
		HttpAssert.body(describePerson, containsString("About:"));
	}
	
	@Test
	public void solrShouldBeUp() throws Exception {
		HttpAssert.body("http://localhost/solr/", containsString("Solr Admin"));
	}
}
