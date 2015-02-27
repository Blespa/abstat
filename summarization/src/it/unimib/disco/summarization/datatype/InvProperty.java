package it.unimib.disco.summarization.datatype;

import java.util.HashMap;
import java.util.List;

import com.hp.hpl.jena.ontology.OntProperty;

/**
 * Provides datatype for store InvProperties infos
 */
public class InvProperty {
	
	private HashMap<String,HashMap<String,Integer>> Counter = new HashMap<String,HashMap<String,Integer>>(); //Property -> Context(Relation Type), Total
	private HashMap<OntProperty,List<OntProperty>> ExtractedInvProperty;
	
	public HashMap<OntProperty,List<OntProperty>> getExtractedInvProperty() {
		return ExtractedInvProperty;
	}
	public void setExtractedInvProperty(HashMap<OntProperty,List<OntProperty>> extractedInvProperty) {
		ExtractedInvProperty = extractedInvProperty;
	}
	public HashMap<String,HashMap<String,Integer>> getCounter() {
		return Counter;
	}
	public void setCounter(HashMap<String,HashMap<String,Integer>> counter) {
		Counter = counter;
	}

}
