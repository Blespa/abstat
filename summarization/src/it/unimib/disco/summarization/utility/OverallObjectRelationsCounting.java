package it.unimib.disco.summarization.utility;

import java.io.File;
import java.util.Vector;

public class OverallObjectRelationsCounting implements Processing{

	private Vector<NTripleAnalysis> propertiesCount;
	private Vector<NTripleAnalysis> akpCounts;
	private File propertyFile;
	private File akps;
	private File minimalTypes;
	
	public OverallObjectRelationsCounting(File propertyFile, File akps, File types) {
		this.propertiesCount = new Vector<NTripleAnalysis>();
		this.akpCounts = new Vector<NTripleAnalysis>();
		this.propertyFile = propertyFile;
		this.akps = akps;
		this.minimalTypes = types;
	}
	
	@Override
	public void process(InputFile file) throws Exception {
		PropertyCount propertyCount = new PropertyCount();
		
		String prefix = new Files().prefixOf(file);
		File minimalTypesFile = new File(minimalTypes, prefix + "_minType.txt");
		AKPObjectCount akpCount = new AKPObjectCount(new TextInput(new FileSystemConnector(minimalTypesFile)));
		
		new NTripleFile(propertyCount, akpCount).process(file);
		
		propertiesCount.add(propertyCount);
		akpCounts.add(akpCount);
	}
	
	public void endProcessing() throws Exception {
		new AggregatedCount(propertiesCount).writeTo(propertyFile);
		new AggregatedCount(akpCounts).writeTo(akps);
	}
}
