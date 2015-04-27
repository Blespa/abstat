package it.unimib.disco.summarization.output;

import it.unimib.disco.summarization.utility.DatatypeCount;
import it.unimib.disco.summarization.utility.InputFile;
import it.unimib.disco.summarization.utility.NTripleAnalysis;
import it.unimib.disco.summarization.utility.NTripleFile;
import it.unimib.disco.summarization.utility.Processing;

import java.io.File;
import java.util.Vector;

public class OverallDatatypeCounts implements Processing{
	
	private Vector<NTripleAnalysis> counts;
	private File file;
	
	public OverallDatatypeCounts(File targetFile) {
		this.counts = new Vector<NTripleAnalysis>();
		this.file = targetFile;
	}
	
	@Override
	public void process(InputFile file) throws Exception {
		DatatypeCount analysis = new DatatypeCount();
		new NTripleFile(analysis).process(file);
		counts.add(analysis);
	}
	
	public void endProcessing() throws Exception {
		new AggregatedCount(counts).writeTo(file);
	}
}