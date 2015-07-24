package it.unimib.disco.summarization.web;

import java.io.File;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;

public class FileResponse implements Response{
	
	private File file;

	public FileResponse(String file){
		this.file = new File("views/" + file);
	}
	
	public InputStream stream() throws Exception{
		return FileUtils.openInputStream(file);
	}
}