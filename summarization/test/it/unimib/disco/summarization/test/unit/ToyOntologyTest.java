package it.unimib.disco.summarization.test.unit;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.*;

import org.junit.Test;

public class ToyOntologyTest {

	@Test
	public void shouldBeSerializedAsOWL() {
		ToyOntology ontology = new ToyOntology()
				.owl()
				.definingConcept("http://any")
				.aSubconceptOf("http://other");
		
		assertThat(ontology.serialize(), containsString("owl#Class"));
	}
	
	@Test
	public void shouldBeSerializedAsRDFS() {
		ToyOntology ontology = new ToyOntology()
				.rdfs()
				.definingConcept("http://any")
				.aSubconceptOf("http://other");
		
		assertThat(ontology.serialize(), containsString("rdf-schema#Class"));
	}
}
