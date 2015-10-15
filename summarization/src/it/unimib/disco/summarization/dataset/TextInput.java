package it.unimib.disco.summarization.dataset;

import java.io.File;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;

public class TextInput implements InputFile{

	private LineIterator lines;
	private String name;

	public TextInput(FileSystemConnector connector) throws Exception {
		this.lines = IOUtils.lineIterator(connector.open(), "UTF-8");
		this.name = connector.absoluteName();
	}

	@Override
	public String name(){
		return name;
	}
	
	@Override
	public String nextLine() throws Exception {
		return lines.nextLine();
	}

	@Override
	public boolean hasNextLine() {
		return lines.hasNext();
	}

	@Override
	public InputFile reopen() throws Exception {
		return new TextInput(new FileSystemConnector(new File(name())));
	}
}
