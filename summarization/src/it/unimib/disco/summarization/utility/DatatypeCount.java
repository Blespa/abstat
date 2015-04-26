package it.unimib.disco.summarization.utility;

import java.util.HashMap;

import com.hp.hpl.jena.sparql.util.NodeFactoryExtra;
import com.hp.hpl.jena.vocabulary.RDFS;

public class DatatypeCount implements NTripleAnalysis {

	private HashMap<String, Long> counts;

	public DatatypeCount() {
		counts = new HashMap<String, Long>();
	}
	
	public HashMap<String, Long> counts() {
		return counts;
	}

	@Override
	public NTripleAnalysis track(NTriple triple) {
		String datatype = NodeFactoryExtra.parseNode(triple.object().uri()).getLiteralDatatypeURI();
		if(datatype == null) datatype = RDFS.Literal.getURI();
		if(!counts.containsKey(datatype)) counts.put(datatype, 0l);
		counts.put(datatype, counts.get(datatype) + 1);
		return this;
	}
}
