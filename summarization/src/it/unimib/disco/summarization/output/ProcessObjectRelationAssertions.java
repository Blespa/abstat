package it.unimib.disco.summarization.output;

import it.unimib.disco.summarization.starter.Events;
import it.unimib.disco.summarization.utility.OverallObjectRelationsCounting;
import it.unimib.disco.summarization.utility.ParallelProcessing;

import java.io.File;

public class ProcessObjectRelationAssertions {
	
	public static void main(String[] args) throws Exception {
		
		new Events();
		
		File sourceDirectory = new File(args[0]);
		File minimalTypesDirectory = new File(args[1]);
		File properties = new File(new File(args[2]), "count-object-properties.txt");
		File akps = new File(new File(args[2]), "object-akp.txt");
		
		OverallObjectRelationsCounting counts = new OverallObjectRelationsCounting(properties, akps, minimalTypesDirectory);
		
		new ParallelProcessing(sourceDirectory, "_obj_properties.nt").process(counts);
	    
	    counts.endProcessing();
	}	
}
