package it.unimib.disco.summarization.systemTests;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Test;

public class SolrIndexingTest {
	
	@Test
	public void solrConceptsIndexingShouldBeOk() throws Exception {
		assertThat(httpResponseFrom("http://localhost/solr/indexing/select?q=type:concept"), containsString("numFound=\"20\""));
	}
	
	@Test
	public void solrDatatypePropertiesIndexingShouldBeOk() throws Exception {
		assertThat(httpResponseFrom("http://localhost/solr/indexing/select?q=type:datatypeProperty"), containsString("numFound=\"11\""));
	}
	
	@Test
	public void solrObjectPropertiesIndexingShouldBeOk() throws Exception {
		assertThat(httpResponseFrom("http://localhost/solr/indexing/select?q=type:objectProperty"), containsString("numFound=\"5\""));
	}
	
	@Test
	public void solrDatatypeAkpsIndexingShouldBeOk() throws Exception {
		assertThat(httpResponseFrom("http://localhost/solr/indexing/select?q=type:datatypeAkp"), containsString("numFound=\"68\""));
	}
	
	@Test
	public void solrObjectAkpsIndexingShouldBeOk() throws Exception {
		assertThat(httpResponseFrom("http://localhost/solr/indexing/select?q=type:objectAkp"), containsString("numFound=\"109\""));
	}
	
	@Test
	public void solrIndexingShouldBeOk() throws Exception {
		assertThat(httpResponseFrom("http://localhost/solr/indexing/select?q=*:*"), containsString("numFound=\"213\""));
	}
	
	private String httpResponseFrom(String address) throws Exception{
		return StringUtils.join(IOUtils.readLines(new DefaultHttpClient().execute(new HttpGet(address)).getEntity().getContent()), "\n");
	}
}
