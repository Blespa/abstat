package it.unimib.disco.summarization.systemTests;

import static org.hamcrest.Matchers.containsString;

import org.junit.Ignore;

public class APITest {

	@Ignore
	public void conceptsAPIShouldReturnUris() throws Exception {
		new HttpAssert("http://localhost").body("/api/v1/concepts", containsString("URI"));
	}
	
	@Ignore
	public void conceptsAPIShouldBeAccessible() throws Exception {
		new HttpAssert("http://localhost").statusOf("/api/v1/concepts", 200);
	}
}
