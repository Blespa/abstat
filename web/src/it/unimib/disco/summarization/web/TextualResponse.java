package it.unimib.disco.summarization.web;

import java.io.InputStream;

import org.apache.commons.io.IOUtils;

public class TextualResponse implements Response{
	
	private String message;
	
	public TextualResponse(String message) {
		this.message = message;
	}
	
	public InputStream stream() throws Exception{
		return IOUtils.toInputStream(message);
	}
}