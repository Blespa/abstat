package it.unimib.disco.summarization.test.system;

import static org.hamcrest.Matchers.containsString;
import it.unimib.disco.summarization.test.web.HttpAssert;

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
	public void dataTypesShouldBeIndexed() throws Exception {
		httpAssert().body("select?q=type:datatype", containsString("numFound=\"6\""));
	}
	
	@Test
	public void solrIndexingShouldBeOk() throws Exception {
		httpAssert().body("select?q=*:*", containsString("numFound=\"219\""));
	}
	
	@Test
	public void shouldIndexFrequencies() throws Exception {
		httpAssert().body("select?q=*:*", containsString("occurrence"));
	}
	
	@Test
	public void shouldIndexConceptFrequencies() throws Exception {
		httpAssert().body("select?q=type:concept", containsString("occurrence\">1"));
	}
	
	@Test
	public void shouldIndexDatatypesFrequencies() throws Exception {
		httpAssert().body("select?q=type:datatype", containsString("occurrence\">2"));
	}
	
	@Test
	public void shouldIndexObjectPropertiesFrequencies() throws Exception {
		httpAssert().body("select?q=type:objectProperty", containsString("occurrence\">1"));
	}
	
	@Test
	public void shouldIndexDatatypePropertiesFrequencies() throws Exception {
		httpAssert().body("select?q=type:datatypeProperty", containsString("occurrence\">1"));
	}
	
	@Test
	public void shouldIndexDataTypeAKPFrequencies() throws Exception {
		httpAssert().body("select?q=type:datatypeAkp", containsString("occurrence\">1"));
	}
	
	@Test
	public void shouldIndexObjectAKPFrequencies() throws Exception {
		httpAssert().body("select?q=type:objectAkp", containsString("occurrence\">1"));
	}
	
	@Test
	public void elementsShouldBeSortableByOccurrence() throws Exception {
		httpAssert().body("select?q=*:*&sort=occurrence+desc", containsString("numFound=\"219\""));
	}
	
	private HttpAssert httpAssert() {
		return new HttpAssert("http://localhost/solr/indexing");
	}
}
