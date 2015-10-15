package it.unimib.disco.summarization.test.web;

import java.net.URI;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

public class ClientCommunication{
	
	private String host;
	private DefaultHttpClient client;

	public ClientCommunication(String host){
		this.host = host;
		HttpParams connection = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(connection, 2000);
		this.client = new DefaultHttpClient(connection);
	}
	
	public HttpResponse httpGet(String path) throws Exception{
		HttpRequestBase request = new HttpGet();
		request.setURI(new URI(host + "/" + path));
		return client.execute(request);
	}	
	
	public List<Cookie> getCookiesFrom(String path) throws Exception{
		httpGet(path);
		return client.getCookieStore().getCookies();
	}
}