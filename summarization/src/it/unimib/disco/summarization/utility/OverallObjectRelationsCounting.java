package it.unimib.disco.summarization.utility;

import java.io.File;
import java.util.Vector;

public class OverallObjectRelationsCounting implements Processing{

	private Vector<NTripleAnalysis> propertiesCount;
	private File propertyFile;
	
	public OverallObjectRelationsCounting(File propertyFile) {
		this.propertiesCount = new Vector<NTripleAnalysis>();
		this.propertyFile = propertyFile;
	}
	
	@Override
	public void process(InputFile file) throws Exception {
		PropertyCount propertyCount = new PropertyCount();
		new NTripleFile(propertyCount).process(file);
		propertiesCount.add(propertyCount);
	}
	
	public void endProcessing() throws Exception {
		new AggregatedCount(propertiesCount).writeTo(propertyFile);
	}
}
