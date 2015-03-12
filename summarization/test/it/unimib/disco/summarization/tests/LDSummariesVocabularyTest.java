package it.unimib.disco.summarization.tests;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import it.unimib.disco.summarization.output.LDSummariesVocabulary;

import org.junit.Before;
import org.junit.Test;

import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;

public class LDSummariesVocabularyTest {

	LDSummariesVocabulary vocabulary;
	
	@Before
	public void setUp(){
		vocabulary = new LDSummariesVocabulary(ModelFactory.createDefaultModel(), "the-dataset");
	}
	
	@Test
	public void shouldCreateAnAKP() {
		
		Resource akp = vocabulary.akpConcept();
		
		assertThat(akp.getURI(), equalTo("http://schemasummaries.org/ontology/AbstractKnowledgePattern"));
	}
	
	@Test
	public void shouldCreateTheFrequencyProperty() throws Exception {
		
		Resource frequency = vocabulary.frequency();
		
		assertThat(frequency.getURI(), equalTo("http://schemasummaries.org/ontology/instanceOccurrence"));
	}
	
	@Test
	public void shouldCreateAnAKPInstance() throws Exception {
		
		Resource akpInstance = vocabulary.akpInstance("http://example.org/Subject", "http://example.org/property", "http://example.org/Object");
		
		assertThat(akpInstance.getURI(), equalTo("http://schemasummaries.org/resource/the-dataset/AKP_Subject_property_Object"));
	}
}
