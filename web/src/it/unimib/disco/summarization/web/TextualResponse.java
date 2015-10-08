package it.unimib.disco.summarization.web;

import org.apache.commons.io.IOUtils;

public class TextualResponse implements Response{
	
	private String message;
	
	public TextualResponse(String message) {
		this.message = message;
	}

	@Override
	public void sendResponse(Communication communication) throws Exception {
		communication.setOutputStream(IOUtils.toInputStream(message));
		communication.setHandled();
	}
}