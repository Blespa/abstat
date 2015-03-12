package it.unimib.disco.summarization.tests;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;
import it.unimib.disco.summarization.output.LDSummariesVocabulary;

import org.junit.Before;
import org.junit.Test;

import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;

public class LDSummariesVocabularyTest {

	LDSummariesVocabulary vocabulary;
	
	@Before
	public void setUp(){
		vocabulary = new LDSummariesVocabulary(ModelFactory.createDefaultModel(), "http://whatever.org/the-dataset");
	}
	
	@Test
	public void shouldCreateAnAKP() {
		
		Resource akp = vocabulary.akpConcept();
		
		assertThat(akp.getURI(), equalTo("http://schemasummaries.org/ontology/AbstractKnowledgePattern"));
	}
	
	@Test
	public void shouldCreateTheFrequencyProperty() throws Exception {
		
		Resource frequency = vocabulary.occurrences();
		
		assertThat(frequency.getURI(), equalTo("http://schemasummaries.org/ontology/instanceOccurrence"));
	}
	
	@Test
	public void shouldCreateAnAKPInstance() throws Exception {
		
		Resource akpInstance = vocabulary.akpInstance("http://example.org/Subject", "http://example.org/property", "http://example.org/Object");
		
		assertThat(akpInstance.getURI(), equalTo("http://schemasummaries.org/resource/the-dataset/AKP_Subject_property_Object"));
	}
	
	@Test
	public void shouldCreateAType() throws Exception {
		
		Resource type = vocabulary.type();
		
		assertThat(type.getURI(), equalTo("http://schemasummaries.org/ontology/Type"));
	}
	
	@Test
	public void shouldCreateMinTypeSubOccurrenceProperty() throws Exception {
		
		Resource property = vocabulary.minTypeSubOccurrence();
		
		assertThat(property.getURI(), equalTo("http://schemasummaries.org/ontology/minTypeSubOccurrence"));
	}
	
	@Test
	public void shouldCreateMinTypeObjOccurrenceProperty() throws Exception {
		
		Resource property = vocabulary.minTypeObjOccurrence();
		
		assertThat(property.getURI(), equalTo("http://schemasummaries.org/ontology/minTypeObjOccurrence"));
	}
	
	@Test
	public void shouldDatatypeConcept() throws Exception {
		
		Resource type = vocabulary.datatype();
		
		assertThat(type.getURI(), equalTo("http://schemasummaries.org/ontology/Datatype"));
	}
	
	@Test
	public void shouldCreateAnAAKP() throws Exception {
		
		Resource type = vocabulary.aakpConcept();
		
		assertThat(type.getURI(), equalTo("http://schemasummaries.org/ontology/AggregatedAbstractKnowledgePattern"));
	}
	
	@Test
	public void shouldCreateAnAAKPInstance() throws Exception {
		
		Resource akpInstance = vocabulary.aakpInstance("http://example.org/Subject", "http://example.org/Object");
		
		assertThat(akpInstance.getURI(), equalTo("http://schemasummaries.org/resource/the-dataset/AAKP_Subject_Object"));
	}
	
	@Test
	public void shouldCreateASignature() throws Exception {
		
		Resource signature = vocabulary.signature();
		
		assertThat(signature.getURI(), equalTo("http://schemasummaries.org/ontology/Signature"));
	}
	
	@Test
	public void shouldCreateAfrequency() throws Exception {
		Resource frequency = vocabulary.frequency();
		
		assertThat(frequency.getURI(), equalTo("http://schemasummaries.org/ontology/frequency"));
	}
	
	@Test
	public void shouldCreateARatio() throws Exception {
		Resource ratio = vocabulary.ratio();
		
		assertThat(ratio.getURI(), equalTo("http://schemasummaries.org/ontology/ratio"));
	}
}
