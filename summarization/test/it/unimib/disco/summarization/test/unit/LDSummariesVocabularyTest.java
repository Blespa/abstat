package it.unimib.disco.summarization.test.unit;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.*;
import it.unimib.disco.summarization.ontology.LDSummariesVocabulary;

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
		
		Resource frequency = vocabulary.occurrence();
		
		assertThat(frequency.getURI(), equalTo("http://ld-summaries.org/ontology/occurrence"));
	}
	
	@Test
	public void shouldCreateAType() throws Exception {
		
		Resource type = vocabulary.type();
		
		assertThat(type.getURI(), equalTo("http://ld-summaries.org/ontology/Type"));
	}
	
	@Test
	public void shouldCreateMinTypeSubOccurrenceProperty() throws Exception {
		
		Resource property = vocabulary.subjectOccurrence();
		
		assertThat(property.getURI(), equalTo("http://ld-summaries.org/ontology/subjectOccurrence"));
	}
	
	@Test
	public void shouldCreateMinTypeObjOccurrenceProperty() throws Exception {
		
		Resource property = vocabulary.objectOccurrence();
		
		assertThat(property.getURI(), equalTo("http://ld-summaries.org/ontology/objectOccurrence"));
	}
	
	@Test
	public void shouldCreateSubjectMinTypesProperty() throws Exception {
		
		Resource property = vocabulary.subjectMinTypes();
		
		assertThat(property.getURI(), equalTo("http://ld-summaries.org/ontology/subjectMinTypes"));
	}
	
	@Test
	public void shouldCreateObjectMinTypesProperty() throws Exception {
		
		Resource property = vocabulary.objectMinTypes();
		
		assertThat(property.getURI(), equalTo("http://ld-summaries.org/ontology/objectMinTypes"));
	}
	
	@Test
	public void shouldDatatypeConcept() throws Exception {
		
		Resource type = vocabulary.datatype();
		
		assertThat(type.getURI(), equalTo("http://ld-summaries.org/ontology/Datatype"));
	}
	
	@Test
	public void shouldCreateAnAKP() {
		
		Resource akp = vocabulary.abstractKnowledgePattern();
		
		assertThat(akp.getURI(), equalTo("http://ld-summaries.org/ontology/AbstractKnowledgePattern"));
	}
	
	@Test
	public void shouldCreateAnAAKP() throws Exception {
		
		Resource type = vocabulary.aggregatePattern();
		
		assertThat(type.getURI(), equalTo("http://ld-summaries.org/ontology/AggregatePattern"));
	}
	
	@Test
	public void shouldCreateAnAAKPInstance() throws Exception {
		
		Resource akpInstance = vocabulary.aakpInstance("http://example.org/Subject", "http://example.org/Object");
		
		assertThat(akpInstance.getURI(), equalTo("http://ld-summaries.org/resource/the-dataset/AP/a48609f690994c9e2d54ee70b1125707"));
	}
	
	@Test
	public void shouldCreateAnAKPInstance() throws Exception {
		
		Resource akpInstance = vocabulary.akpInstance("http://example.org/Subject", "http://example.org/property", "http://example.org/Object");
		
		assertThat(akpInstance.getURI(), equalTo("http://ld-summaries.org/resource/the-dataset/AKP/81372da753a8fabedfa1afefa050c2cc"));
	}
	
	@Test
	public void twoAKPWithSameLocalNamesShouldBeDifferent() throws Exception {
		
		Resource akp = vocabulary.akpInstance("http://aa.org/Subject", "http://aa.org/property", "http://aa.org/Object");
		Resource akpWithSameLocalName = vocabulary.akpInstance("http://aa.org/Subject", "http://bb.org/property", "http://aa.org/Object");
		
		assertThat(akp.getURI(), not(equalTo(akpWithSameLocalName.getURI())));
	}
	
	@Test
	public void shouldGetTheLocalResourceForAGlobalResource() throws Exception {
		
		Resource localConcept = vocabulary.asLocalResource("http://www.w3.org/2002/07/owl#Thing");
		
		assertThat(localConcept.getURI(), equalTo("http://ld-summaries.org/resource/the-dataset/www.w3.org/2002/07/owl#Thing"));
	}
	
	@Test
	public void shouldWriteAProperty() throws Exception {
		
		Resource property = vocabulary.property();
		
		assertThat(property.getURI(), equalTo("http://ld-summaries.org/ontology/Property"));
	}
	
	@Test
	public void shouldGetAConcept() throws Exception {
		
		Resource property = vocabulary.concept();
		
		assertThat(property.getURI(), equalTo("http://www.w3.org/2004/02/skos/core#Concept"));
	}
	
	@Test
	public void shouldGetTheSubjectProperty() throws Exception {
		
		Resource subject = vocabulary.subject();
		
		assertThat(subject.getURI(), equalTo("http://www.w3.org/1999/02/22-rdf-syntax-ns#subject"));
	}
	
	@Test
	public void shouldGetTheObjectProperty() throws Exception {
		
		Resource subject = vocabulary.object();
		
		assertThat(subject.getURI(), equalTo("http://www.w3.org/1999/02/22-rdf-syntax-ns#object"));
	}
	
	@Test
	public void shouldGetThePredicateProperty() throws Exception {
		
		Resource subject = vocabulary.predicate();
		
		assertThat(subject.getURI(), equalTo("http://www.w3.org/1999/02/22-rdf-syntax-ns#predicate"));
	}
	
	@Test
	public void shouldGetTheTypedEntity() throws Exception {
		
		Resource typed = vocabulary.selfOrUntyped("http://aaa.org/Concept");
		
		assertThat(typed.getURI(), equalTo("http://aaa.org/Concept"));
	}
	
	@Test
	public void shouldGetTheUntypedConcept() throws Exception {
		
		Resource typed = vocabulary.selfOrUntyped("Ukn_Type");
		
		assertThat(typed.getURI(), equalTo("http://www.w3.org/2000/01/rdf-schema#Literal"));
	}
	
	@Test
	public void shouldCreateADatatypeProperty() throws Exception {
		
		Resource property = vocabulary.asLocalDatatypeProperty("http://dbpedia.org/property/name");
		
		assertThat(property.getURI(), containsString("datatype-property/dbpedia.org/property/name"));
	}
	
	@Test
	public void shouldCreateAnObjectProperty() throws Exception {
		
		Resource property = vocabulary.asLocalObjectProperty("http://dbpedia.org/property/name");
		
		assertThat(property.getURI(), containsString("object-property/dbpedia.org/property/name"));
	}
	
	@Test
	public void shouldExposeExternalResourcesType() throws Exception {
		
		Resource type = vocabulary.external();
		
		assertThat(type.getURI(), containsString("External"));
	}
	
	@Test
	public void shouldExposeInternalResourcesType() throws Exception {
		
		Resource type = vocabulary.internal();
		
		assertThat(type.getURI(), containsString("Internal"));
	}
}
