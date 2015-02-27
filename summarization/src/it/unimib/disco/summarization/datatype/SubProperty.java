package it.unimib.disco.summarization.datatype;

import java.util.HashMap;
import java.util.List;

import com.hp.hpl.jena.ontology.OntProperty;

/**
 * Provides datatype for store SubProperties infos
 */
public class SubProperty {
	
	private HashMap<String,HashMap<String,Integer>> Counter = new HashMap<String,HashMap<String,Integer>>(); //Property -> Context(Relation Type), Total
	private HashMap<OntProperty,List<OntProperty>> ExtractedSubProperty;
	
	public HashMap<OntProperty,List<OntProperty>> getExtractedSubProperty() {
		return ExtractedSubProperty;
	}
	public void setExtractedSubProperty(HashMap<OntProperty,List<OntProperty>> extractedSubProperty) {
		ExtractedSubProperty = extractedSubProperty;
	}
	public HashMap<String,HashMap<String,Integer>> getCounter() {
		return Counter;
	}
	public void setCounter(HashMap<String,HashMap<String,Integer>> counter) {
		Counter = counter;
	}

}
