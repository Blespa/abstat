package it.unimib.disco.summarization.tests;

import it.unimib.disco.summarization.utility.InputFile;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TextInputTestDouble implements InputFile{

	private List<String> lines = new ArrayList<String>(); 
	private Iterator<String> currentLine;
	
	public TextInputTestDouble withLine(String line){
		this.lines.add(line);
		return this;
	}
	
	@Override
	public String nextLine() throws Exception {
		if(currentLine == null) currentLine = lines.iterator();
		return currentLine.next();
	}

	@Override
	public boolean hasNextLine() {
		if(currentLine == null) currentLine = lines.iterator();
		return currentLine.hasNext();
	}

	@Override
	public String name() {
		return "the_name";
	}

	@Override
	public InputFile reopen() throws Exception {
		return this;
	}
}