package it.unimib.disco.summarization.test.system;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import it.unimib.disco.summarization.test.web.HttpAssert;

import org.junit.Test;

public class SolrFullTextSearchTest {

	@Test
	public void shouldSupportQueriesOnProperties() throws Exception {
		
		httpAssert().body("select?q=fullTextSearchField:place&fq=type:objectProperty", containsString("http://dbpedia.org/ontology/birthPlace"));
	}

	@Test
	public void shouldSupportQueriesOnConcepts() throws Exception {
		
		httpAssert().body("select?q=fullTextSearchField:place&fq=type:concept", containsString("http://dbpedia.org/ontology/PopulatedPlace"));
	}
	
	@Test
	public void shouldSupportQueriesOnAKPs() throws Exception {
		
		httpAssert().body("select?q=fullTextSearchField:place&fq=type:objectAkp", containsString("http://dbpedia.org/ontology/capital"));
	}
	
	@Test
	public void shouldSupportQueriesOnCrossTypologies() throws Exception {
		
		httpAssert().body("select?q=fullTextSearchField:place", allOf(containsString("concept"),
																	  containsString("Property"),
																	  containsString("Akp")));
	}
	
	@Test
	public void conceptsFromDBpediaShouldBeMarkedAsInternal() throws Exception {
		httpAssert().body("select?q=URI:%22http://dbpedia.org/ontology/Place%22", containsString("internal"));
	}
	
	@Test
	public void conceptsFromSchemaOrgShouldBeMarkedAsExternal() throws Exception {
		httpAssert().body("select?q=URI:%22http://schema.org/Place%22", containsString("external"));
	}
	
	@Test
	public void datatypeAKPsReferringToExternalSubkectsShouldBeMarkedAsExternal() throws Exception {
		httpAssert().body("select?q=*:*&fq=type:datatypeAkp&fq=subtype:external", containsString("schema.org"));
	}
	
	@Test
	public void datatypeAKPsReferringToInternalSubkectsShouldBeMarkedAsInternal() throws Exception {
		httpAssert().body("select?q=*:*&fq=type:datatypeAkp&fq=subtype:internal", containsString("dbpedia.org/ontology/OfficeHolder"));
	}
	
	@Test
	public void updateUrlShouldNotBeAccessible() throws Exception {
		
		httpAssert().statusOf("update", 404);
	}
	
	private HttpAssert httpAssert() {
		return new HttpAssert("http://localhost/solr/indexing");
	}
}
