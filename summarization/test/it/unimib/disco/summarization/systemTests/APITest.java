package it.unimib.disco.summarization.systemTests;

import static org.hamcrest.Matchers.containsString;

import org.junit.Test;

public class APITest {

	@Test
	public void conceptsAPIShouldReturnUris() throws Exception {
		new HttpAssert("http://localhost").body("/api/v1/autocomplete/concepts?dataset=system-test&q=ci", containsString("http://dbpedia.org/ontology/City"));
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
		new HttpAssert("http://localhost").body("/api/v1/autocomplete/properties?dataset=system-test&q=reatot", containsString("datatypeProperty"));
	}

}
