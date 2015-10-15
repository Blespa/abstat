package it.unimib.disco.summarization.test.unit;

import it.unimib.disco.summarization.dataset.InputFile;
import it.unimib.disco.summarization.dataset.Processing;

import java.util.ArrayList;
import java.util.List;

public class ProcessingInspector implements Processing{

	List<InputFile> processedFile = new ArrayList<InputFile>();
	
	@Override
	public void process(InputFile file) throws Exception {
		processedFile.add(file);
	}

	@Override
	public void endProcessing() throws Exception {}
	
	public int countProcessed(){
		return processedFile.size();
	}
}