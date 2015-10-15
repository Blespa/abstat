package it.unimib.disco.summarization.test.unit;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;
import it.unimib.disco.summarization.ontology.RDFResource;

import org.junit.Test;

public class RDFResourceTest {

	@Test
	public void shouldGiveTheLabelOfDBPediaProperties() throws Exception {
		RDFResource result = new RDFResource("http://dbpedia.org/property/name");
		
		assertThat(result.localName(), equalTo("name"));
	}
	
	@Test
	public void shouldGiveTheLabelOfDBPediaOntologyProperties() throws Exception {
		RDFResource result = new RDFResource("http://dbpedia.org/ontology/name");
		
		assertThat(result.localName(), equalTo("name"));
	}
	
	@Test
	public void shouldGiveTheLabelOnDublinCoreSubject() throws Exception {
		RDFResource result = new RDFResource("http://www.w3.org/2004/02/skos/core#subject");		
		
		assertThat(result.localName(), equalTo("subject"));
	}

	@Test
	public void shouldGetTheNamespaceOfDublinCoreTerms() throws Exception {
		RDFResource result = new RDFResource("http://www.w3.org/2004/02/skos/core#subject");
		
		assertThat(result.namespace(), equalTo("http://www.w3.org/2004/02/skos/core#"));
	}
	
	@Test
	public void shouldGetTheNamespaceOfDBPediaTerms() throws Exception {
		RDFResource result = new RDFResource("http://dbpedia.org/ontology/name");
		
		assertThat(result.namespace(), equalTo("http://dbpedia.org/ontology/"));
	}
	
	@Test
	public void shouldParseTheSchemaSummariesNamedGraph() throws Exception {
		
		RDFResource result = new RDFResource("http://schemasummaries.org/dbpedia-2014");
		
		assertThat(result.localName(), equalTo("dbpedia-2014"));
	}
}
