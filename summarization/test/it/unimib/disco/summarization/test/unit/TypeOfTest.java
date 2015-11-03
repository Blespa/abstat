package it.unimib.disco.summarization.test.unit;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import it.unimib.disco.summarization.ontology.TypeOf;

import org.junit.Test;

public class TypeOfTest {

	@Test
	public void aResourceThatContainsThePayLevelDomainIsInternal() {
		
		String type = new TypeOf("domain.org").resource("http://domain.org/Class");
		
		assertThat(type, equalTo("internal"));
	}

	@Test
	public void aResourceThatDoesNotContainThePayLevelDomainIsExternal() {
		
		String type = new TypeOf("domain.org").resource("http://schema.org/Class");
		
		assertThat(type, equalTo("external"));
	}
	
	@Test
	public void dbpediaWikidataSpecialCase() {
		
		String type = new TypeOf("domain.org").resource("http://domain.org/Wikidata/Class");
		
		assertThat(type, equalTo("external"));
	}
	
	@Test
	public void anObjectAKPIsInternalIfBothTheSubjectAndTheObjectAreInternal() {
		
		String type = new TypeOf("dbpedia.org").objectAKP("http://dbpedia.org/Person", "http://dbpedia.org/City");
		
		assertThat(type, equalTo("internal"));
	}

	@Test
	public void anObjectAKPIsExternalIfTheSubjectIsExternal() {
		
		String type = new TypeOf("dbpedia.org").objectAKP("http://schema.org/Person", "http://dbpedia.org/City");
		
		assertThat(type, equalTo("external"));
	}
	
	@Test
	public void anObjectAKPIsExternalIfTheObjectIsExternal() {
		
		String type = new TypeOf("dbpedia.org").objectAKP("http://dbpedia.org/Person", "http://schema.org/City");
		
		assertThat(type, equalTo("external"));
	}
	
	@Test
	public void dbpediaWikidataSpecialCaseForSubject() {
		
		String type = new TypeOf("dbpedia.org").objectAKP("http://dbpedia.org/Wikidata/Person", "http://dbpedia.org/City");
		
		assertThat(type, equalTo("external"));
	}
	
	@Test
	public void aDatatypeAKPIsInternalIfTheSubjectIsInternal() {
		
		String type = new TypeOf("dbpedia.org").datatypeAKP("http://dbpedia.org/Person");
		
		assertThat(type, equalTo("internal"));
	}
	
	@Test
	public void aDatatypeAKPIsExternalIfTheSubjectIsExternal() {
		
		String type = new TypeOf("dbpedia.org").datatypeAKP("http://schema.org/Person");
		
		assertThat(type, equalTo("external"));
	}
}
