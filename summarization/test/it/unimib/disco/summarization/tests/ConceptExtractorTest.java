package it.unimib.disco.summarization.tests;

import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import it.unimib.disco.summarization.extraction.ConceptExtractor;

import java.util.HashMap;

import org.junit.Test;

public class ConceptExtractorTest {

	@Test
	public void shouldSpotAOWLClearDefinedConcept() {
		
		TestOntology model = new TestOntology()
									.rdfs()
									.definingConcept("http://the.class");
		
		HashMap<String, String> concepts = conceptsFrom(model);
		
		assertThat(concepts.get("http://the.class"), notNullValue());
	}
	
	@Test
	public void shouldSpotAlsoImplicitTypeDeclarations() throws Exception {
		
		TestOntology model = new TestOntology()
									.rdfs()
									.definingResource("http://father")
									.aSubconceptOf("http://parent");
		
		HashMap<String, String> concepts = conceptsFrom(model);
		
		assertThat(concepts.get("http://father"), notNullValue());
		assertThat(concepts.get("http://parent"), notNullValue());
	}
	
	private HashMap<String, String> conceptsFrom(TestOntology model) {
		
		ConceptExtractor conceptExtractor = new ConceptExtractor();
		conceptExtractor.setConcepts(model.build());
		
		return conceptExtractor.getConcepts();
	}

}
