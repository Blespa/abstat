package it.unimib.disco.summarization.output;

import it.unimib.disco.summarization.utility.TextInput;

public interface Processing {

	public void process(TextInput file) throws Exception;
	
	public void endProcessing() throws Exception;
}