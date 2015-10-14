package it.unimib.disco.summarization.systemTests;

import static org.hamcrest.Matchers.containsString;

import org.junit.Ignore;
import org.junit.Test;

public class SolrIndexingTest {
	
	@Test
	public void solrConceptsIndexingShouldBeOk() throws Exception {
		httpAssert().body("select?q=type:concept", containsString("numFound=\"20\""));
	}

	@Test
	public void solrDatatypePropertiesIndexingShouldBeOk() throws Exception {
		httpAssert().body("select?q=type:datatypeProperty", containsString("numFound=\"11\""));
	}
	
	@Test
	public void solrObjectPropertiesIndexingShouldBeOk() throws Exception {
		httpAssert().body("select?q=type:objectProperty", containsString("numFound=\"5\""));
	}
	
	@Test
	public void solrDatatypeAkpsIndexingShouldBeOk() throws Exception {
		httpAssert().body("select?q=type:datatypeAkp", containsString("numFound=\"68\""));
	}
	
	@Test
	public void solrObjectAkpsIndexingShouldBeOk() throws Exception {
		httpAssert().body("select?q=type:objectAkp", containsString("numFound=\"109\""));
	}
	
	@Test
	@Ignore
	public void dataTypesShouldBeIndexed() throws Exception {
		httpAssert().body("select?q=type:datatype", containsString("numFound=\"6\""));
	}
	
	@Test
	public void solrIndexingShouldBeOk() throws Exception {
		httpAssert().body("select?q=*:*", containsString("numFound=\"213\""));
	}
	
	private HttpAssert httpAssert() {
		return new HttpAssert("http://localhost/solr/indexing");
	}
}
