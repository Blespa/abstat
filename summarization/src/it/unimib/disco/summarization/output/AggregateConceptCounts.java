package it.unimib.disco.summarization.output;

import it.unimib.disco.summarization.utility.ConceptCount;
import it.unimib.disco.summarization.utility.Files;

import java.io.File;

public class AggregateConceptCounts {

	public static void main(String[] args) throws Exception {
		
		Events.summarization();
		
		File sourceDirectory = new File(args[0]);
		File targetFile = new File(args[1], "count-concepts.txt");
		
		ConceptCount counts = new ConceptCount();
		for(File file : new Files().get(sourceDirectory, "_countConcepts.txt")){
			try{
				counts.process(file);
			}catch(Exception e){
				Events.summarization().error("processing " + file, e);
			}
		}
		counts.writeResultsTo(targetFile);
	}
}
