package it.unimib.disco.summarization.test.unit;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;
import it.unimib.disco.summarization.ontology.InternalAKP;

import org.junit.Test;

public class InternalAKPTest {

	@Test
	public void anObjectAKPIsInternalIfBothTheSubjectAndTheObjectAreInternal() {
		
		String type = new InternalAKP("dbpedia.org").typeOf("http://dbpedia.org/Person", "http://dbpedia.org/City");
		
		assertThat(type, equalTo("internal"));
	}

	@Test
	public void anObjectAKPIsExternalIfTheSubjectIsExternal() {
		
		String type = new InternalAKP("dbpedia.org").typeOf("http://schema.org/Person", "http://dbpedia.org/City");
		
		assertThat(type, equalTo("external"));
	}
	
	@Test
	public void anObjectAKPIsExternalIfTheObjectIsExternal() {
		
		String type = new InternalAKP("dbpedia.org").typeOf("http://dbpedia.org/Person", "http://schema.org/City");
		
		assertThat(type, equalTo("external"));
	}
	
	@Test
	public void dbpediaWikidataSpecialCaseForSubject() {
		
		String type = new InternalAKP("dbpedia.org").typeOf("http://dbpedia.org//wikidata/Person", "http://dbpedia.org/City");
		
		assertThat(type, equalTo("external"));
	}
	
	@Test
	public void dbpediaWikidataSpecialCaseForObject() {
		
		String type = new InternalAKP("dbpedia.org").typeOf("http://dbpedia.org/Person", "http://dbpedia.org/wikidata/City");
		
		assertThat(type, equalTo("external"));
	}
}
