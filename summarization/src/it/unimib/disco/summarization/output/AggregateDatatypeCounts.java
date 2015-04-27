package it.unimib.disco.summarization.output;

import java.io.File;

public class AggregateDatatypeCounts {

	public static void main(String[] args) throws Exception {
		
		File sourceDirectory = new File(args[0]);
		File targetFile = new File(new File(args[1]), "count-datatype.txt");
		String suffix = "_dt_properties.nt";
		
		final OverallDatatypeCounts counts = new OverallDatatypeCounts(targetFile);
		
		new ParallelProcessing(sourceDirectory, suffix).process(counts);
	    
	    counts.writeResults();
	}	
}
