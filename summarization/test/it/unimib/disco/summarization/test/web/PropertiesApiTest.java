package it.unimib.disco.summarization.test.web;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;
import it.unimib.disco.summarization.web.PropertiesApi;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

public class PropertiesApiTest {

	@Test
	public void getAutocompleteSuggestion() throws Exception {
		RequestTestDouble request = new RequestTestDouble()
											.withParameter("q", "bir")
											.withParameter("dataset", "system-test");
		
		assertThat(IOUtils.toString(new PropertiesApi(new ConnectorTestDouble()).getResponseFromConnector(request)), containsString("http://dbpedia.org/ontology/birthDate"));
	}
}
