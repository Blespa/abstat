package it.unimib.disco.summarization.test.unit;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import it.unimib.disco.summarization.ontology.InternalResources;

import org.junit.Test;

public class InternalResourcesTest {

	@Test
	public void aResourceThatContainsThePayLevelDomainIsInternal() {
		
		String type = new InternalResources("domain.org").typeOf("http://domain.org/Class");
		
		assertThat(type, equalTo("internal"));
	}

	@Test
	public void aResourceThatDoesNotContainThePayLevelDomainIsExternal() {
		
		String type = new InternalResources("domain.org").typeOf("http://schema.org/Class");
		
		assertThat(type, equalTo("external"));
	}
	
	@Test
	public void dbpediaWikidataSpecialCase() {
		
		String type = new InternalResources("domain.org").typeOf("http://domain.org/wikidata/Class");
		
		assertThat(type, equalTo("external"));
	}
}
