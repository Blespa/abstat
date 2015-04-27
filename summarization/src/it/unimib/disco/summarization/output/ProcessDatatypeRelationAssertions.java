package it.unimib.disco.summarization.output;
import it.unimib.disco.summarization.starter.Events;
import it.unimib.disco.summarization.utility.OverallDatatypeRelationsCounting;
import it.unimib.disco.summarization.utility.ParallelProcessing;

import java.io.File;

public class ProcessDatatypeRelationAssertions {

	public static void main(String[] args) throws Exception {
		
		new Events();
		
		File sourceDirectory = new File(args[0]);
		File minimalTypesDirectory = new File(args[1]);
		File datatypes = new File(new File(args[2]), "count-datatype.txt");
		File properties = new File(new File(args[2]), "count-datatype-properties.txt");
		File akps = new File(new File(args[2]), "datatype-akp.txt");
		
		OverallDatatypeRelationsCounting counts = new OverallDatatypeRelationsCounting(datatypes, properties, akps, minimalTypesDirectory);
		
		new ParallelProcessing(sourceDirectory, "_dt_properties.nt").process(counts);
	    
	    counts.endProcessing();
	}	
}
