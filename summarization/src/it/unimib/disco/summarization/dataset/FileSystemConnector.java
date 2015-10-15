package it.unimib.disco.summarization.dataset;

import java.io.File;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;

public class FileSystemConnector{
	
	private File file;

	public FileSystemConnector(File file){
		this.file = file;
	}
	
	public InputStream open() throws Exception{
		return FileUtils.openInputStream(this.file);
	}

	public String absoluteName() {
		return file.getAbsolutePath();
	}
}