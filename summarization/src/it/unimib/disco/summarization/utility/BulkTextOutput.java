package it.unimib.disco.summarization.utility;

import java.util.ArrayList;

public class BulkTextOutput implements OutputFile{

	private ArrayList<String> lines;
	private int threshold;
	private FileSystemConnector connector;

	public BulkTextOutput(FileSystemConnector connector, int threshold) {
		this.lines = new ArrayList<String>();
		this.threshold = threshold;
		this.connector = connector;
	}
	
	@Override
	public OutputFile writeLine(String content) throws Exception {
		lines.add(content);
		if(lines.size() >= threshold) this.close();
		return this;
	}

	@Override
	public void close() throws Exception {
		TextOutput out = new TextOutput(connector);
		for(String line : lines){
			out.writeLine(line);
		}
		out.close();
		lines = new ArrayList<String>();
	}
}