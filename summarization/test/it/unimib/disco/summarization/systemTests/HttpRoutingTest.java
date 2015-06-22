package it.unimib.disco.summarization.systemTests;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Test;

public class HttpRoutingTest {

	@Test
	public void theApplicationShouldBeUp() throws Exception {
		
		assertThat(httpResponseFrom("http://localhost"), containsString("ABSTAT"));
	}
	
	@Test
	public void nonStandardPortShouldBeRedirectToStandardPort() throws Exception {
		assertThat(httpResponseFrom("http://localhost:8880"), containsString("ABSTAT"));
	}
	
	@Test
	public void theSparqlEndpointShouldBeUp() throws Exception {
		assertThat(httpResponseFrom("http://localhost/sparql"), containsString("SPARQL"));
	}
	
	@Test
	public void describeCapabilityShouldBeExposed() throws Exception {
		
		String response = httpResponseFrom("http://localhost/describe/?uri=http%3A%2F%2Fld-summaries.org%2Fresource%2Fdbpedia-2014%2Fwww.ontologydesignpatterns.org%2Font%2Fdul%2FDUL.owl%23NaturalPerson");
		
		assertThat(response, containsString("About:"));
	}
	
	@Test
	public void solrShouldBeUp() throws Exception {
		assertThat(httpResponseFrom("http://localhost/solr/"), containsString("Solr Admin"));
	}

	private String httpResponseFrom(String address) throws Exception{
		return StringUtils.join(IOUtils.readLines(new DefaultHttpClient().execute(new HttpGet(address)).getEntity().getContent()), "\n");
	}
}
