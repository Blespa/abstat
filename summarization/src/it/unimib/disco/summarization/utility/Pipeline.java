package it.unimib.disco.summarization.utility;


public class Pipeline implements Processing{

	private Processing[] processings;

	public Pipeline(Processing... processing) {
		this.processings = processing;
	}

	@Override
	public void process(InputFile inputFile) throws Exception {
		for(Processing processing : processings){
			processing.process(inputFile);
		}
	}

	@Override
	public void endProcessing() throws Exception {
		for(Processing processing : processings){
			processing.endProcessing();
		}
	}	
}