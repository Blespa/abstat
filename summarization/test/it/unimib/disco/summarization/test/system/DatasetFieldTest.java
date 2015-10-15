package it.unimib.disco.summarization.test.system;

import static org.hamcrest.Matchers.containsString;
import it.unimib.disco.summarization.test.web.HttpAssert;

import org.junit.Test;

public class DatasetFieldTest {

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
		httpAssert().body("select?q=dataset:system-test", containsString("numFound=\"219\""));
	}
	
	private HttpAssert httpAssert() {
		return new HttpAssert("http://localhost/solr/indexing");
	}
}
