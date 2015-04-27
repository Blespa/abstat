package it.unimib.disco.summarization.utility;


public interface Processing {

	public void process(TextInput file) throws Exception;
	
	public void endProcessing() throws Exception;
}