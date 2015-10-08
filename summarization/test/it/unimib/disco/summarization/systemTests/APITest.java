package it.unimib.disco.summarization.systemTests;

import static org.hamcrest.Matchers.containsString;

import org.junit.Ignore;

public class APITest {

	@Ignore
	public void conceptsAPIShouldReturnUris() throws Exception {
		new HttpAssert("http://localhost").body("/api/v1/autocomplete/concepts?dataset=system-test&q=city", containsString("http://dbpedia.org/ontology/City"));
	}

}
