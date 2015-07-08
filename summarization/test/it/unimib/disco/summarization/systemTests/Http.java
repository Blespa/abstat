package it.unimib.disco.summarization.systemTests;

import static org.junit.Assert.assertThat;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.hamcrest.Matcher;

public class Http {

	public static void assertBody(String url, Matcher<String> constraint) throws Exception {
		assertThat(responseFrom(url), constraint);
	}
	
	public static String responseFrom(String address) throws Exception{
		return StringUtils.join(IOUtils.readLines(new DefaultHttpClient().execute(new HttpGet(address)).getEntity().getContent()), "\n");
	}
}
