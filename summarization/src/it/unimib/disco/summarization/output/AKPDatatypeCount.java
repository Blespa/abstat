package it.unimib.disco.summarization.output;

import it.unimib.disco.summarization.utility.InputFile;
import it.unimib.disco.summarization.utility.MinimalTypes;
import it.unimib.disco.summarization.utility.NTriple;
import it.unimib.disco.summarization.utility.NTripleAnalysis;

import java.util.HashMap;

public class AKPDatatypeCount implements NTripleAnalysis{

	private MinimalTypes types;
	private HashMap<String, Long> akps;

	public AKPDatatypeCount(InputFile minimalTypes) throws Exception {
		this.types = new MinimalTypes(minimalTypes);
		this.akps = new HashMap<String, Long>();
	}

	public HashMap<String, Long> counts() {
		return akps;
	}

	public AKPDatatypeCount track(NTriple triple) {
		String datatype = triple.dataType();
		String subject = triple.subject().toString();
		String property = triple.property().toString();
		
		for(String type : types.of(subject)){
			String key = type + "##" + property + "##" + datatype;
			if(!akps.containsKey(key)) akps.put(key, 0l);
			akps.put(key, akps.get(key) + 1);
		}
		return this;
	}
}
