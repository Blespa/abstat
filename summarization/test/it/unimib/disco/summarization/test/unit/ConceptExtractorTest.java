package it.unimib.disco.summarization.test.unit;

import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.*;
import it.unimib.disco.summarization.ontology.ConceptExtractor;

import java.util.HashMap;

import org.junit.Test;

public class ConceptExtractorTest {

	@Test
	public void shouldSpotAOWLClearDefinedConcept() {
		
		ToyOntology model = new ToyOntology()
									.rdfs()
									.definingConcept("http://the.class");
		
		HashMap<String, String> concepts = conceptsFrom(model);
		
		assertThat(concepts.get("http://the.class"), notNullValue());
	}
	
	@Test
	public void shouldSpotAlsoImplicitTypeDeclarations() throws Exception {
		
		ToyOntology model = new ToyOntology()
									.rdfs()
									.definingResource("http://father")
									.aSubconceptOf("http://parent");
		
		HashMap<String, String> concepts = conceptsFrom(model);
		
		assertThat(concepts.get("http://father"), notNullValue());
		assertThat(concepts.get("http://parent"), notNullValue());
	}
	
	@Test
	public void shouldSpotAnEquivalentClass() throws Exception {
		
		ToyOntology ontology = new ToyOntology()
										.owl()
										.definingConcept("http://dbpedia.org/Person")
										.equivalentTo("http://schema.org/Person");
		
		HashMap<String, String> concepts = conceptsFrom(ontology);
		
		assertThat(concepts.get("http://schema.org/Person"), notNullValue());
	}
	
	private HashMap<String, String> conceptsFrom(ToyOntology model) {
		
		ConceptExtractor conceptExtractor = new ConceptExtractor();
		conceptExtractor.setConcepts(model.build());
		
		return conceptExtractor.getConcepts();
	}

}
