package it.unimib.disco.summarization.web;

import java.io.File;

import org.apache.commons.io.FileUtils;

public class FileResponse implements Response{
	
	private File file;

	public FileResponse(String file){
		this.file = new File("views/" + file);
	}
	
	@Override
	public void sendResponse(Communication communication) throws Exception {
		communication.setOutputStream(FileUtils.openInputStream(file));
		communication.setHandled();
	}
}