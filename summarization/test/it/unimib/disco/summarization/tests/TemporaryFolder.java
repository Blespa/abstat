package it.unimib.disco.summarization.tests;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

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
	
	public File namedFile(String content, String name) throws Exception{
		File file = new File(directory(), name);
		FileUtils.write(file, content);
		return file;
	}
	
	public File file(){
		return createRandomFileWithExtension("");
	}

	public File file(String content) throws Exception{
		return file(content, "");
	}

	public File file(String content, String extension) throws IOException {
		File file = createRandomFileWithExtension(extension);
		FileUtils.write(file, content);
		return file;
	}
	
	public Collection<File> files(String suffix) {
		return FileUtils.listFiles(directory(), new String[]{suffix}, false);
	}
	
	private File createRandomFileWithExtension(String extension) {
		return new File(directory(), Math.random() + "." + extension);
	}
}