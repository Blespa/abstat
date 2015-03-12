package it.unimib.disco.summarization.tests;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
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
		vocabulary = new LDSummariesVocabulary(ModelFactory.createDefaultModel(), "http://whatever.org/the-dataset");
	}
	
	@Test
	public void shouldCreateTheFrequencyProperty() throws Exception {
		
		Resource frequency = vocabulary.instanceOccurrence();
		
		assertThat(frequency.getURI(), equalTo("http://schemasummaries.org/ontology/instanceOccurrence"));
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
	public void shouldCreateAnAKP() {
		
		Resource akp = vocabulary.akpConcept();
		
		assertThat(akp.getURI(), equalTo("http://schemasummaries.org/ontology/AbstractKnowledgePattern"));
	}
	
	@Test
	public void shouldCreateAnAAKP() throws Exception {
		
		Resource type = vocabulary.aakpConcept();
		
		assertThat(type.getURI(), equalTo("http://schemasummaries.org/ontology/AggregatedAbstractKnowledgePattern"));
	}
	
	@Test
	public void shouldCreateAnAAKPInstance() throws Exception {
		
		Resource akpInstance = vocabulary.aakpInstance("http://example.org/Subject", "http://example.org/Object");
		
		assertThat(akpInstance.getURI(), equalTo("http://schemasummaries.org/resource/the-dataset/AAKP/a48609f690994c9e2d54ee70b1125707"));
	}
	
	@Test
	public void shouldCreateAnAKPInstance() throws Exception {
		
		Resource akpInstance = vocabulary.akpInstance("http://example.org/Subject", "http://example.org/property", "http://example.org/Object");
		
		assertThat(akpInstance.getURI(), equalTo("http://schemasummaries.org/resource/the-dataset/AKP/81372da753a8fabedfa1afefa050c2cc"));
	}
	
	@Test
	public void twoAKPWithSameLocalNamesShouldBeDifferent() throws Exception {
		
		Resource akp = vocabulary.akpInstance("http://aa.org/Subject", "http://aa.org/property", "http://aa.org/Object");
		Resource akpWithSameLocalName = vocabulary.akpInstance("http://aa.org/Subject", "http://bb.org/property", "http://aa.org/Object");
		
		assertThat(akp.getURI(), not(equalTo(akpWithSameLocalName.getURI())));
	}
	
	@Test
	public void shouldCreateASignature() throws Exception {
		
		Resource signature = vocabulary.signature();
		
		assertThat(signature.getURI(), equalTo("http://schemasummaries.org/ontology/Signature"));
	}
	
	@Test
	public void shouldGetTheLocalResourceForAGlobalResource() throws Exception {
		
		Resource localConcept = vocabulary.asLocalResource("http://www.w3.org/2002/07/owl#Thing");
		
		assertThat(localConcept.getURI(), equalTo("http://schemasummaries.org/resource/the-dataset/www.w3.org/2002/07/owl#Thing"));
	}
	
	@Test
	public void shouldGetTheSubjectInstanceOccurrence() throws Exception {
		
		Resource property = vocabulary.subjectInstanceOccurrence();
		
		assertThat(property.getURI(), equalTo("http://schemasummaries.org/ontology/subjectInstanceOccurrence"));
	}
}
