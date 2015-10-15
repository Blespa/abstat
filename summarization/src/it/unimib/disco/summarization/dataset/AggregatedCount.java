package it.unimib.disco.summarization.dataset;

import java.io.File;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Vector;

public class AggregatedCount{
	
	private Vector<NTripleAnalysis> counts;
	
	public AggregatedCount(Vector<NTripleAnalysis> counts) {
		this.counts = counts;
	}
	
	public void writeTo(File file) throws Exception {
		HashMap<String, Long> aggregatedCounts = new HashMap<String, Long>();
		for(NTripleAnalysis count : counts){
	    	for(Entry<String, Long> occurrences : count.counts().entrySet()){
	    		String datatype = occurrences.getKey();
				if(!aggregatedCounts.containsKey(datatype)) aggregatedCounts.put(datatype, 0l);
				aggregatedCounts.put(datatype, aggregatedCounts.get(datatype) + occurrences.getValue());
	    	}
	    }
	    
		BulkTextOutput output = new BulkTextOutput(new FileSystemConnector(file), 100);
	    for(Entry<String, Long> count : aggregatedCounts.entrySet()){
	    	output.writeLine(count.getKey() + "##" + count.getValue());
    	}
	    output.close();
	}
}