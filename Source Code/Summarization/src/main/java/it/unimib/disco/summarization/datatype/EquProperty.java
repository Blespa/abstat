package it.unimib.disco.summarization.datatype;

import java.util.HashMap;
import java.util.List;

import com.hp.hpl.jena.ontology.OntProperty;

/**
 * Provides datatype for store EquProperties infos
 */
public class EquProperty {
	
	private HashMap<String,HashMap<String,Integer>> Counter = new HashMap<String,HashMap<String,Integer>>(); //Property -> Context(Relation Type), Total
	private HashMap<OntProperty,List<OntProperty>> ExtractedEquProperty;
	
	public HashMap<OntProperty,List<OntProperty>> getExtractedEquProperty() {
		return ExtractedEquProperty;
	}
	public void setExtractedEquProperty(HashMap<OntProperty,List<OntProperty>> extractedEquProperty) {
		ExtractedEquProperty = extractedEquProperty;
	}
	public HashMap<String,HashMap<String,Integer>> getCounter() {
		return Counter;
	}
	public void setCounter(HashMap<String,HashMap<String,Integer>> counter) {
		Counter = counter;
	}

}
