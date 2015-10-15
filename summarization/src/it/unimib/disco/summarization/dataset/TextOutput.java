package it.unimib.disco.summarization.dataset;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

public class TextOutput{

	private List<String> lines;
	private FileSystemConnector connector;

	public TextOutput(FileSystemConnector connector) {
		this.lines = new ArrayList<String>();
		this.connector = connector;
	}
	
	public TextOutput writeLine(String content) throws Exception {
		lines.add(content.toString());
		return this;
	}

	public void close() throws Exception {
		FileUtils.writeLines(new File(connector.absoluteName()), lines, true);
		lines = new ArrayList<String>();
	}
}
