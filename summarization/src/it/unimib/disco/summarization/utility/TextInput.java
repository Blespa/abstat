package it.unimib.disco.summarization.utility;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;

public class TextInput{

	private LineIterator lines;

	public TextInput(FileSystemConnector connector) throws Exception {
		this.lines = IOUtils.lineIterator(connector.open(), "UTF-8");
	}

	public String nextLine() throws Exception {
		return lines.nextLine();
	}

	public boolean hasNextLine() {
		return lines.hasNext();
	}
}
