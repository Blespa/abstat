package it.unimib.disco.summarization.ontology;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.hp.hpl.jena.ontology.OntClass;

/**
 * Provides datatype for store SubClassOf infos
 */
public class SubClassOf {
	
	private ArrayList<List<OntClass>> ConceptsSubclassOf = new ArrayList<List<OntClass>>();
	
	
	//Save SubClassOf Relation (concept SubClassOf conceptSup)
	public void addSubClassOfRelation(OntClass concept, OntClass conceptSup){
		List<OntClass> subRelation = new ArrayList<OntClass>();
		subRelation.add(concept); //SubConcept
		subRelation.add(conceptSup); //SuperConcept
		ConceptsSubclassOf.add(subRelation);
	}
	
	public ArrayList<List<OntClass>> getConceptsSubclassOf() {
		return ConceptsSubclassOf;
	}

	public void setConceptsSubclassOf(ArrayList<List<OntClass>> conceptsSubclassOf) {
		ConceptsSubclassOf = conceptsSubclassOf;
	}

	//Pulisco le relazioni di sottoclasse di Thing
	public void deleteThing(){
		Iterator<List<OntClass>> ScIter = ConceptsSubclassOf.iterator();

		while (ScIter.hasNext()) {
			List<OntClass> curEl = ScIter.next();
			String localNameSup = curEl.get(1).getLocalName();

			if(localNameSup.equals("Thing")){
				ScIter.remove();
			}
		}
	}	

}
