package it.unimib.disco.summarization.output;

import it.unimib.disco.summarization.starter.Events;
import it.unimib.disco.summarization.utility.OverallObjectRelationsCounting;
import it.unimib.disco.summarization.utility.ParallelProcessing;

import java.io.File;

public class ProcessObjectRelationAssertions {
	
	public static void main(String[] args) throws Exception {
		
		new Events();
		
		File sourceDirectory = new File(args[0]);
		File properties = new File(new File(args[1]), "count-object-properties.txt");
		
		OverallObjectRelationsCounting counts = new OverallObjectRelationsCounting(properties);
		
		new ParallelProcessing(sourceDirectory, "_obj_properties.nt").process(counts);
	    
	    counts.endProcessing();
	}	
}
