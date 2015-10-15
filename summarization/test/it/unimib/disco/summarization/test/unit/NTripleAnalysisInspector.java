package it.unimib.disco.summarization.test.unit;

import it.unimib.disco.summarization.dataset.NTriple;
import it.unimib.disco.summarization.dataset.NTripleAnalysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NTripleAnalysisInspector implements NTripleAnalysis{

	private List<NTriple> processed = new ArrayList<NTriple>();
	
	@Override
	public NTripleAnalysis track(NTriple triple) {
		processed.add(triple);
		return this;
	}
	
	public int countProcessed(){
		return processed.size();
	}

	@Override
	public HashMap<String, Long> counts() {
		return null;
	}
}