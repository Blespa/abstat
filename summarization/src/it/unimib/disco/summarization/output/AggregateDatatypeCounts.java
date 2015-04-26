package it.unimib.disco.summarization.output;

import it.unimib.disco.summarization.starter.Events;
import it.unimib.disco.summarization.utility.BulkTextOutput;
import it.unimib.disco.summarization.utility.DatatypeCount;
import it.unimib.disco.summarization.utility.FileSystemConnector;
import it.unimib.disco.summarization.utility.NTripleFile;

import java.io.File;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AggregateDatatypeCounts {

	public static void main(String[] args) throws Exception {
		
		File sourceDirectory = new File(args[0]);
		File targetFile = new File(new File(args[1]), "count-datatype.txt");
		
		final Events logger = new Events();
		final Vector<DatatypeCount> counts = new Vector<DatatypeCount>();
		ExecutorService executor = Executors.newFixedThreadPool(10);
		for(final File file : new Files().get(sourceDirectory, "_dt_properties.nt")){
			executor.execute(new Runnable() {
				@Override
				public void run() {
					try {
						DatatypeCount analysis = new DatatypeCount();
						new NTripleFile(analysis).process(file);
						counts.add(analysis);
					} catch (Exception e) {
						logger.error(file, e);
					}
				}
			});
		}
		executor.shutdown();
	    while(!executor.isTerminated()){}
	    
	    HashMap<String, Long> aggregatedCounts = new HashMap<String, Long>();
	    for(DatatypeCount count : counts){
	    	for(Entry<String, Long> datatypeOccurrences : count.counts().entrySet()){
	    		String datatype = datatypeOccurrences.getKey();
				if(!aggregatedCounts.containsKey(datatype)) aggregatedCounts.put(datatype, 0l);
				aggregatedCounts.put(datatype, aggregatedCounts.get(datatype) + datatypeOccurrences.getValue());
	    	}
	    }
	    BulkTextOutput output = new BulkTextOutput(new FileSystemConnector(targetFile), 100);
	    for(Entry<String, Long> count : aggregatedCounts.entrySet()){
	    	output.writeLine(count.getKey() + "##" + count.getValue());
    	}
	    output.close();
	}
}
