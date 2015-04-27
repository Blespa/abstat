package it.unimib.disco.summarization.ontology;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.hp.hpl.jena.ontology.OntResource;

/**
 * Provides datatype for store EquConcept infos
 */
public class EquivalentConcepts {
	
	private HashMap<OntResource,List<OntResource>> ExtractedEquConcept;
	private ArrayList<String> equConcept = new ArrayList<String>();
	
	public HashMap<OntResource,List<OntResource>> getExtractedEquConcept() {
		return ExtractedEquConcept;
	}
	public void setExtractedEquConcept(HashMap<OntResource,List<OntResource>> extractedEquConcept) {
		ExtractedEquConcept = extractedEquConcept;
	}
	public ArrayList<String> getEquConcept() {
		return equConcept;
	}
	public void setEquConcept(ArrayList<String> equConcept) {
		this.equConcept = equConcept;
	}

}
