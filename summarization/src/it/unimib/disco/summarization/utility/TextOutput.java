package it.unimib.disco.summarization.utility;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

public class TextOutput implements OutputFile {

	private List<String> lines;
	private FileSystemConnector connector;

	public TextOutput(FileSystemConnector connector) {
		this.lines = new ArrayList<String>();
		this.connector = connector;
	}
	
	@Override
	public OutputFile writeLine(String content) throws Exception {
		lines.add(content.toString());
		return this;
	}

	@Override
	public void close() throws Exception {
		FileUtils.writeLines(new File(connector.absoluteName()), lines, true);
		lines = new ArrayList<String>();
	}
}
