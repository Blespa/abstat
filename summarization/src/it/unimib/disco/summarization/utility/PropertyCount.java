package it.unimib.disco.summarization.utility;

import java.util.HashMap;

public class PropertyCount implements NTripleAnalysis{

	private HashMap<String, Long> counts;

	public PropertyCount() {
		counts = new HashMap<String, Long>();
	}
	
	public HashMap<String, Long> counts() {
		return counts;
	}
	
	public PropertyCount track(NTriple triple) {
		String property = triple.property().asResource().getURI();
		if(!counts.containsKey(property)) counts.put(property, 0l);
		counts.put(property, counts.get(property) + 1);
		return this;
	}
}
