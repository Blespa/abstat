package it.unimib.disco.summarization.systemTests;

import static org.hamcrest.Matchers.containsString;

import org.junit.Test;

public class DatasetFieldOkTest {

	@Test
	public void shouldBeOkTheDatasetFieldOfSchemaSolrTest1() throws Exception
	{
		httpAssert().body("select?q=dataset:test", containsString("numFound=\"0\""));
	}
	
	@Test
	public void shouldBeOkTheDatasetFieldOfSchemaSolrTest2() throws Exception
	{
		httpAssert().body("select?q=dataset:system", containsString("numFound=\"0\""));
	}
	
	@Test
	public void shouldBeOkTheDatasetFieldOfSchemaSolrTest4() throws Exception
	{
		httpAssert().body("select?q=dataset:system-test", containsString("numFound=\"213\""));
	}
	
	private HttpAssert httpAssert() {
		return new HttpAssert("http://localhost/solr/indexing");
	}
}
