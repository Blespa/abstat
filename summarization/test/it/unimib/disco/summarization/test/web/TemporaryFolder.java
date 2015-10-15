package it.unimib.disco.summarization.test.web;

import java.io.File;

import org.apache.commons.io.FileUtils;

public class TemporaryFolder{
	
	public File directory() {
		return new File("tmp");
	}
	
	public TemporaryFolder create(){
		directory().mkdir();
		return this;
	}
	
	public TemporaryFolder delete(){
		FileUtils.deleteQuietly(directory());
		return this;
	}
	
	public String path(){
		return directory().getAbsolutePath();
	}
}