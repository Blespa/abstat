package it.unimib.disco.summarization.datatype;

import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.rdf.model.RDFNode;

/**
 * Provides datatype for store MinCardinality with Literal relation infos
 */
public class LiteralAxiom {
	
	private ArrayList<List<RDFNode>> Axiom = new ArrayList<List<RDFNode>> ();
	
	//Save Axiom Relation (conceptSubj Axiom(property) conceptObj)
	public void addAxiomRelation(OntClass conceptSubj, OntProperty property, RDFNode LiteralObj){
		List<RDFNode> newAxiom = new ArrayList<RDFNode>();
		newAxiom.add(conceptSubj); //Subject
		newAxiom.add(property); //Property
		newAxiom.add(LiteralObj); //Literal Object
		Axiom.add(newAxiom);
	}

	public ArrayList<List<RDFNode>> getLiteralAxiom() {
		return Axiom;
	}

	public void setLiteralAxiom(ArrayList<List<RDFNode>> axiom) {
		Axiom = axiom;
	}

}
