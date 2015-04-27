package it.unimib.disco.summarization.utility;

import java.io.File;
import java.util.Vector;

public class OverallDatatypeRelationsCounting implements Processing{
	
	private Vector<NTripleAnalysis> datatypesCount;
	private Vector<NTripleAnalysis> propertiesCount;
	private File datatypeCountsResults;
	private File propertyFile;
	
	public OverallDatatypeRelationsCounting(File datatypeFile, File propertyFile) {
		this.datatypesCount = new Vector<NTripleAnalysis>();
		this.propertiesCount = new Vector<NTripleAnalysis>();
		this.datatypeCountsResults = datatypeFile;
		this.propertyFile = propertyFile;
	}
	
	@Override
	public void process(InputFile file) throws Exception {
		DatatypeCount analysis = new DatatypeCount();
		PropertyCount propertyCount = new PropertyCount();
		new NTripleFile(analysis, propertyCount).process(file);
		datatypesCount.add(analysis);
		propertiesCount.add(propertyCount);
	}
	
	public void endProcessing() throws Exception {
		new AggregatedCount(datatypesCount).writeTo(datatypeCountsResults);
		new AggregatedCount(propertiesCount).writeTo(propertyFile);
	}
}