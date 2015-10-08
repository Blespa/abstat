package it.unimib.disco.summarization.web.tests;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;
import it.unimib.disco.summarization.web.JsonResponse;

import org.junit.Test;

public class ConceptsApiTest {
	
	@Test
	public void getAutocompleteSuggestion() throws Exception {
		CommunicationTestDouble communication = new CommunicationTestDouble();
		new JsonResponse("any").sendResponse(communication);
		
		assertThat(communication.getResponse(), containsString("http://dbpedia.org/ontology/City"));
	}
}
