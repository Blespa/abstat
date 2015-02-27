package it.unimib.disco.summarization.datatype;

import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 * Provides datatype for store SomeValuesFrom, AllValuesFrom, MinCardinality relation infos
 */
public class Axiom {
	
	private ArrayList<List<Resource>> Axiom = new ArrayList<List<Resource>> ();
	
	//Save Axiom Relation (conceptSubj Axiom(property) conceptObj)
	public void addAxiomRelation(OntClass conceptSubj, OntProperty property, OntClass conceptObj){
		List<Resource> newAxiom = new ArrayList<Resource>();
		newAxiom.add(conceptSubj); //Subject
		newAxiom.add(property); //Property
		newAxiom.add(conceptObj); //Object
		Axiom.add(newAxiom);
	}

	public ArrayList<List<Resource>> getAxiom() {
		return Axiom;
	}

	public void setAxiom(ArrayList<List<Resource>> axiom) {
		Axiom = axiom;
	}

}
