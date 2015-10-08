package it.unimib.disco.summarization.web.tests;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

import it.unimib.disco.summarization.web.Communication;

public class CommunicationTestDouble implements Communication{

	private InputStream response;

	@Override
	public void setOutputStream(InputStream content) {
		this.response = content;
	}

	public String getResponse() throws IOException {
		return IOUtils.toString(response);
	}

	@Override
	public InputStream getAutocomplete(String path) {
		return IOUtils.toInputStream("\"response\": {" + 
											"\"docs\": [{" +
													"\"URI\":[\"http://dbpedia.org/ontology/City\"]}" +
											"]" + 
									"}");
	}
	
	@Override
	public void setHandled() {}
	
	@Override
	public void setContentType(String contentType) {}
}
