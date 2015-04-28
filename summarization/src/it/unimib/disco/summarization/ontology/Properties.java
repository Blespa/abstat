package it.unimib.disco.summarization.ontology;

import java.util.HashMap;
import java.util.List;

import com.hp.hpl.jena.ontology.OntProperty;

/**
 * Provides datatype for store Properties infos
 */
public class Properties {
	
	private HashMap<String,String> Property;
	private HashMap<String,HashMap<String,Integer>> Counter = new HashMap<String,HashMap<String,Integer>>(); //Property -> Context(Relation Type), Total
	private List<OntProperty> ExtractedProperty;
	
	public HashMap<String,String> getProperty() {
		return Property;
	}
	public void setProperty(HashMap<String,String> property) {
		Property = property;
	}
	public List<OntProperty> getExtractedProperty() {
		return ExtractedProperty;
	}
	public void setExtractedProperty(List<OntProperty> extractedProperty) {
		ExtractedProperty = extractedProperty;
	}
	public HashMap<String,HashMap<String,Integer>> getCounter() {
		return Counter;
	}
	public void setCounter(HashMap<String,HashMap<String,Integer>> counter) {
		Counter = counter;
	}

}
