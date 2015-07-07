package it.unimib.disco.summarization.systemTests;

import static org.hamcrest.Matchers.containsString;

import org.junit.Test;

public class SolrIndexingTest {
	
	@Test
	public void solrConceptsIndexingShouldBeOk() throws Exception {
		HttpAssert.body("http://localhost/solr/indexing/select?q=type:concept", containsString("numFound=\"20\""));
	}
	
	@Test
	public void solrDatatypePropertiesIndexingShouldBeOk() throws Exception {
		HttpAssert.body("http://localhost/solr/indexing/select?q=type:datatypeProperty", containsString("numFound=\"11\""));
	}
	
	@Test
	public void solrObjectPropertiesIndexingShouldBeOk() throws Exception {
		HttpAssert.body("http://localhost/solr/indexing/select?q=type:objectProperty", containsString("numFound=\"5\""));
	}
	
	@Test
	public void solrDatatypeAkpsIndexingShouldBeOk() throws Exception {
		HttpAssert.body("http://localhost/solr/indexing/select?q=type:datatypeAkp", containsString("numFound=\"68\""));
	}
	
	@Test
	public void solrObjectAkpsIndexingShouldBeOk() throws Exception {
		HttpAssert.body("http://localhost/solr/indexing/select?q=type:objectAkp", containsString("numFound=\"109\""));
	}
	
	@Test
	public void solrIndexingShouldBeOk() throws Exception {
		HttpAssert.body("http://localhost/solr/indexing/select?q=*:*", containsString("numFound=\"213\""));
	}
}
