package it.unimib.disco.summarization.test.web;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;
import it.unimib.disco.summarization.test.unit.TemporaryFolder;
import it.unimib.disco.summarization.web.Datasets;

import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DatasetsTest {

	private TemporaryFolder temp;

	@Before
	public void createTemporaryFolder(){
		this.temp = new TemporaryFolder().create();
	}
	
	@After
	public void deleteTemporaryFolder(){
		this.temp.delete();
	}
	
	@Test
	public void onEmptyDatasetDirectoryShouldReturnEmpty() throws Exception {
		
		InputStream response = new Datasets(temp.directory()).get(new RequestTestDouble());
		
		List<String> lines = IOUtils.readLines(response);
		
		assertThat(lines.get(0), equalTo("{\"datasets\":[]}"));
	}
	
	@Test
	public void shouldContainTheDatasetAsURI() throws Exception {
		
		InputStream response = new Datasets(temp.add("dbpedia").directory()).get(new RequestTestDouble());
		
		List<String> lines = IOUtils.readLines(response);
		
		assertThat(lines.get(0), equalTo("{\"datasets\":[{\"URI\":\"http://ld-summaries.org/dbpedia\"}]}"));
	}
	
	@Test
	public void shouldGetMultipleDatasets() throws Exception {
		
		InputStream response = new Datasets(temp.add("dbpedia").add("yago").directory()).get(new RequestTestDouble());
		
		List<String> lines = IOUtils.readLines(response);
		
		assertThat(lines.get(0), equalTo("{\"datasets\":[{\"URI\":\"http://ld-summaries.org/dbpedia\"},{\"URI\":\"http://ld-summaries.org/yago\"}]}"));
	}
}
