package it.unimib.disco.summarization.ontology;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDFS;

/**
 * ConceptExtractor: Extract Concept Defined inside Ontology
 */
public class ConceptExtractor {
	
	private HashMap<String,String> Concepts = new HashMap<String,String>();
	private HashMap<String,String> ObtainedBy = new HashMap<String,String>();
	private List<OntClass> ExtractedConcepts = new ArrayList<OntClass>();


	public void setConcepts(OntModel ontologyModel) {
		
		//Get Concept from Model
		enrichWithImplicitClassesDeclarations(ontologyModel, RDFS.subClassOf);
		enrichWithImplicitClassesDeclarations(ontologyModel, OWL.equivalentClass);
		ExtendedIterator<OntClass> TempExtractedConcepts = ontologyModel.listClasses();
		
		
		//Save Useful Info About Concepts
		while(TempExtractedConcepts.hasNext()) {
			OntClass concept = TempExtractedConcepts.next();
			
			String URI = concept.getURI();
			if(URI != null){
				ExtractedConcepts.add(concept);
				Concepts.put(URI,concept.getLocalName());	
				getObtainedBy().put(URI, "Direct");
			}
		}
		
		TempExtractedConcepts.close();
		
	}
	
	private void enrichWithImplicitClassesDeclarations(OntModel ontologyModel, final Property p) {
		StmtIterator statements = ontologyModel.listStatements(new SimpleSelector(){
			@Override
			public boolean selects(Statement s) {
				Property predicate = s.getPredicate();
				
				return predicate.equals(p);
			}
		});
		HashSet<String> implicitClasses = new HashSet<String>();
		while(statements.hasNext()){
			Statement statement = statements.next();
			implicitClasses.add(statement.getSubject().getURI());
			implicitClasses.add(statement.getObject().toString());
		}
		statements.close();
		for(String implicitClass : implicitClasses){
			ontologyModel.createClass(implicitClass);
		}
	}
	
	public List<OntClass> getExtractedConcepts() {
		return ExtractedConcepts;
	}
	
	public HashMap<String, String> getConcepts() {
		return Concepts;
	}

	public HashMap<String,String> getObtainedBy() {
		return ObtainedBy;
	}

	public void setObtainedBy(HashMap<String,String> obtainedBy) {

		ObtainedBy = obtainedBy;
	}
	
}
