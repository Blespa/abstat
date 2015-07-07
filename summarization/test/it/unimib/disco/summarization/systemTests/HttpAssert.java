package it.unimib.disco.summarization.systemTests;

import static org.junit.Assert.assertThat;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.hamcrest.Matcher;

public class HttpAssert {

	public static void body(String url, Matcher<String> constraint) throws Exception {
		assertThat(httpResponseFrom(url), constraint);
	}
	
	private static String httpResponseFrom(String address) throws Exception{
		return StringUtils.join(IOUtils.readLines(new DefaultHttpClient().execute(new HttpGet(address)).getEntity().getContent()), "\n");
	}
}
