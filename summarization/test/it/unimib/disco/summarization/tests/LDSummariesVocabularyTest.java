package it.unimib.disco.summarization.tests;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;
import it.unimib.disco.summarization.output.LDSummariesVocabulary;

import org.junit.Before;
import org.junit.Test;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;

public class LDSummariesVocabularyTest {

	Model model;
	
	@Before
	public void setUp(){
		model = ModelFactory.createDefaultModel();
	}
	
	@Test
	public void shouldCreateAnAKP() {
		
		Resource akp = new LDSummariesVocabulary(model).akpConcept();
		
		assertThat(akp.getURI(), equalTo("http://schemasummaries.org/ontology/AbstractKnowledgePattern"));
	}
	
	@Test
	public void shouldCreateTheFrequencyProperty() throws Exception {
		
		Resource frequency = new LDSummariesVocabulary(model).frequency();
		
		assertThat(frequency.getURI(), equalTo("http://schemasummaries.org/ontology/instanceOccurrence"));
	}
}
