package it.unimib.disco.summarization.dataset;


public interface Processing {

	public void process(InputFile file) throws Exception;
	
	public void endProcessing() throws Exception;
}