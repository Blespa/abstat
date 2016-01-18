package it.unimib.disco.summarization.test.system;

import static org.hamcrest.Matchers.containsString;

import org.junit.Test;

import it.unimib.disco.summarization.test.web.HttpAssert;

public class SampleAPITest {
	
	@Test
	public void shouldWorkWithObjectAKPs() throws Exception {
		new HttpAssert("http://localhost").body("/api/v1/sample?endpoint=http://dbpedia.org/sparql&akp=http://ld-summaries.org/resource/system-test/AKP/6c3d3badd793d15e6da3677886389818", containsString("\"o\": { \"type\": \"uri\" , \"value\":"));
	}
	
	@Test
	public void shouldWorkWithLiteralDatatypeAKPs() throws Exception {
		new HttpAssert("http://localhost").body("/api/v1/sample?endpoint=http://dbpedia.org/sparql&akp=http://ld-summaries.org/resource/system-test/AKP/a1792e5711c7ac1207b7d5a7d5a993c3", containsString("\"o\": { \"type\": \"literal\""));
	}
	
	@Test
	public void shouldWorkWithDatatypeAKPs() throws Exception {
		new HttpAssert("http://localhost").body("/api/v1/sample?endpoint=http://dbpedia.org/sparql&akp=http://ld-summaries.org/resource/system-test/AKP/7fa4ad0b5be445410d925b412d25dedc", containsString("\"o\": { \"datatype\":"));
	}
	
	@Test
	public void sampleShouldShowSubject() throws Exception {
		new HttpAssert("http://localhost").body("/api/v1/sample?endpoint=http://dbpedia.org/sparql&akp=http://ld-summaries.org/resource/system-test/AKP/6c3d3badd793d15e6da3677886389818", containsString("\"s\":"));
	}
	
	@Test
	public void sampleShouldShowPredicate() throws Exception {
		new HttpAssert("http://localhost").body("/api/v1/sample?endpoint=http://dbpedia.org/sparql&akp=http://ld-summaries.org/resource/system-test/AKP/6c3d3badd793d15e6da3677886389818", containsString("\"p\":"));
	}
	
	@Test
	public void sampleShouldShowObject() throws Exception {
		new HttpAssert("http://localhost").body("/api/v1/sample?endpoint=http://dbpedia.org/sparql&akp=http://ld-summaries.org/resource/system-test/AKP/6c3d3badd793d15e6da3677886389818", containsString("\"o\":"));
	}
}