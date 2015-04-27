package it.unimib.disco.summarization.utility;


public interface Processing {

	public void process(InputFile file) throws Exception;
	
	public void endProcessing() throws Exception;
}