package it.unimib.disco.summarization.tests;

import java.io.File;

import org.apache.commons.io.FileUtils;

public class TemporaryFolder{
	
	File temporary = new File("tmp");
	
	public TemporaryFolder create(){
		temporary.mkdir();
		return this;
	}
	
	public TemporaryFolder delete(){
		FileUtils.deleteQuietly(temporary);
		return this;
	}
	
	public String path(){
		return temporary.getAbsolutePath();
	}
	
	public File newFile(){
		return new File(temporary, Math.random() + "");
	}
	
	public File newFile(String content) throws Exception{
		File file = newFile();
		FileUtils.write(file, content);
		return file;
	}
}