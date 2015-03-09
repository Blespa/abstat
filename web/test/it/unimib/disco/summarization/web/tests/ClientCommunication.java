package it.unimib.disco.summarization.web.tests;

import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

public class ClientCommunication{
	
	private String host;

	public ClientCommunication(String host){
		this.host = host;
	}
	
	public HttpResponse httpGet(String path) throws Exception{
		HttpRequestBase request = new HttpGet();
		request.setURI(new URI(host + "/" + path));
		HttpParams connection = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(connection, 2000);
		return new DefaultHttpClient(connection).execute(request);
	}
}