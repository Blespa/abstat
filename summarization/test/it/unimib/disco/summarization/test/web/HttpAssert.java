package it.unimib.disco.summarization.test.web;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.http.cookie.Cookie;
import org.hamcrest.Matcher;

public class HttpAssert{
	
	private String address;

	public HttpAssert(String address){
		this.address = address;
	}
	
	public void statusOf(String path, int code) throws Exception {
		assertThat(new ClientCommunication(address).httpGet(path).getStatusLine().getStatusCode(), is(code));
	}
	
	public void body(String path, Matcher<String> constraint) throws Exception {
		assertThat(IOUtils.toString(new ClientCommunication(address).httpGet(path).getEntity().getContent()), constraint);
	}
	
	public void cookie(String path, String id) throws Exception{
		List<Cookie> cookies = new ClientCommunication(address).getCookiesFrom(path);
		for(Cookie cookie : cookies){
			String name = cookie.getName();
			if(name.equals(id)){
				return;
			}
		}
		fail("Expected a cookie named " + id + ". Got: " + cookies);
	}
}