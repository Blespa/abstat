package it.unimib.disco.summarization.tests;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

public class TemporaryFolder{
	
	private File temporary = new File("tmp");
	
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
		return createRandomFileWithExtension("");
	}

	public File newFile(String content) throws Exception{
		return newFile(content, "");
	}

	public File newFile(String content, String extension) throws IOException {
		File file = createRandomFileWithExtension(extension);
		FileUtils.write(file, content);
		return file;
	}
	
	private File createRandomFileWithExtension(String extension) {
		return new File(temporary, Math.random() + "." + extension);
	}
}