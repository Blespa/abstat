package it.unimib.disco.summarization.dataset;

import java.util.HashMap;

public interface NTripleAnalysis {

	public NTripleAnalysis track(NTriple triple);
	
	public HashMap<String, Long> counts();
}