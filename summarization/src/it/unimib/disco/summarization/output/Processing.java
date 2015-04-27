package it.unimib.disco.summarization.output;

import java.io.File;

public interface Processing {

	public void process(File file) throws Exception;
	
	public void endProcessing() throws Exception;
}