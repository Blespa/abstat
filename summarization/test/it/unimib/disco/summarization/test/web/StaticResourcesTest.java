package it.unimib.disco.summarization.test.web;

import static org.hamcrest.Matchers.equalTo;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class StaticResourcesTest {

	private File file = new File("assets/a-file");
	private SummarizationTestApplication application = new SummarizationTestApplication();

	@Before
	public void setUp() throws Exception{
		FileUtils.write(file, "content");
		application.start();
	}
	
	@After
	public void tearDown() throws Exception{
		application.stop();
		file.delete();
	}
	
	@Test
	public void aStaticResourceShouldBeUnderAssetsDirectoryOnTheServer() throws Exception {
		
		application.httpAssert().body("/static/a-file", equalTo("content"));
	}
}
