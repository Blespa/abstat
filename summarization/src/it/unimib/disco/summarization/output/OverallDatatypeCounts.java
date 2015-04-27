package it.unimib.disco.summarization.output;

import it.unimib.disco.summarization.utility.BulkTextOutput;
import it.unimib.disco.summarization.utility.DatatypeCount;
import it.unimib.disco.summarization.utility.FileSystemConnector;
import it.unimib.disco.summarization.utility.NTripleFile;
import it.unimib.disco.summarization.utility.TextInput;

import java.io.File;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Vector;

public class OverallDatatypeCounts implements Processing{
	
	private Vector<DatatypeCount> counts;
	private File file;
	
	public OverallDatatypeCounts(File targetFile) {
		this.counts = new Vector<DatatypeCount>();
		this.file = targetFile;
	}
	
	@Override
	public void process(TextInput file) throws Exception {
		DatatypeCount analysis = new DatatypeCount();
		new NTripleFile(analysis).process(file);
		counts.add(analysis);
	}
	
	public void endProcessing() throws Exception {
		
		HashMap<String, Long> aggregatedCounts = new HashMap<String, Long>();
	    for(DatatypeCount count : counts){
	    	for(Entry<String, Long> datatypeOccurrences : count.counts().entrySet()){
	    		String datatype = datatypeOccurrences.getKey();
				if(!aggregatedCounts.containsKey(datatype)) aggregatedCounts.put(datatype, 0l);
				aggregatedCounts.put(datatype, aggregatedCounts.get(datatype) + datatypeOccurrences.getValue());
	    	}
	    }
	    BulkTextOutput output = new BulkTextOutput(new FileSystemConnector(file), 100);
	    for(Entry<String, Long> count : aggregatedCounts.entrySet()){
	    	output.writeLine(count.getKey() + "##" + count.getValue());
    	}
	    output.close();
	}
}