package it.unimib.disco.summarization.output;
import it.unimib.disco.summarization.utility.ParallelProcessing;

import java.io.File;

public class ProcessDatatypeRelationAssertions {

	public static void main(String[] args) throws Exception {
		
		File sourceDirectory = new File(args[0]);
		File datatypes = new File(new File(args[1]), "count-datatype.txt");
		File properties = new File(new File(args[1]), "count-datatype-properties.txt");
		
		OverallDatatypeCounts counts = new OverallDatatypeCounts(datatypes, properties);
		
		new ParallelProcessing(sourceDirectory, "_dt_properties.nt").process(counts);
	    
	    counts.endProcessing();
	}	
}
