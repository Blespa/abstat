package it.unimib.disco.summarization.test.system;

import static org.hamcrest.Matchers.containsString;
import it.unimib.disco.summarization.test.web.HttpAssert;

import org.junit.Test;

public class APITest {

	@Test
	public void conceptsAPIShouldReturnUris() throws Exception {
		new HttpAssert("http://localhost").body("/api/v1/autocomplete/concepts?dataset=system-test&q=ci", containsString("http://dbpedia.org/ontology/City"));
	}
	
	@Test
	public void testconceptsAPIShouldReturnTypes() throws Exception {
		new HttpAssert("http://localhost").body("/api/v1/autocomplete/concepts?dataset=system-test&q=ci", containsString("\"type\":\"concept\""));
	}
	
	@Test
	public void conceptsAPIShouldReturnAlsoDatatypes() throws Exception {
		new HttpAssert("http://localhost").body("/api/v1/autocomplete/concepts?dataset=system-test&q=dat", containsString("http://www.w3.org/2001/XMLSchema#date"));
	}
	
	@Test
	public void shouldHandleWhiteSpaces() throws Exception {
		new HttpAssert("http://localhost").body("/api/v1/autocomplete/concepts?dataset=system-test&q=city+of", containsString("http://dbpedia.org/ontology/City"));
	}
	
	@Test
	public void propertiesAPIShouldReturnUris() throws Exception {
		new HttpAssert("http://localhost").body("/api/v1/autocomplete/properties?dataset=system-test&q=reatot", containsString("http://dbpedia.org/ontology/areaTotal"));
	}
	
	@Test
	public void propertiesAPIShouldReturnTypes() throws Exception {
		new HttpAssert("http://localhost").body("/api/v1/autocomplete/properties?dataset=system-test&q=reatot", containsString("\"type\":\"datatypeProperty\""));
	}

}
