package it.unimib.disco.summarization.test.web;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;
import it.unimib.disco.summarization.web.SolrAutocomplete;

import org.junit.Test;

public class SolrAutocompleteTest {
	
	@Test
	public void getAutocompleteSuggestion() throws Exception {
		
		SolrConnectorTestDouble connector = new SolrConnectorTestDouble();
		
		new SolrAutocomplete(connector, "any").get(new RequestTestDouble()
														.withParameter("q", "ci")
														.withParameter("dataset", "system-test"));
		
		assertThat(connector.requestedUrl(), containsString("/solr/indexing/any?q=URI_ngram:ci&fq=dataset:system-test"));
	}
}
