package it.unimib.disco.summarization.test.web;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;
import it.unimib.disco.summarization.web.QueryString;

import org.junit.Test;

public class QueryStringTest {
	
	@Test
	public void emptyQueryString() throws Exception {
		assertThat(new QueryString().build(), equalTo("?"));
	}

	@Test
	public void oneParameter() throws Exception {
		assertThat(new QueryString().addParameter("q", "URI", "city").build(), equalTo("?q=URI:city"));
	}
	
	@Test
	public void twoParameters() throws Exception {
		assertThat(new QueryString()
							.addParameter("q", "URI", "city")
							.addParameter("data", "dataset", "dbpedia")
							.build(), 
				   equalTo("?q=URI:city&data=dataset:dbpedia"));
	}
}
