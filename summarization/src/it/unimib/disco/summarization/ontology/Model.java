package it.unimib.disco.summarization.ontology;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class Model {
	
	OntModel ontologyModel;
	
	public Model(String OwlBaseFile, String FileType){
		ontologyModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM, null); 
		ontologyModel.read(OwlBaseFile, FileType);
	}
	
	public OntModel getOntologyModel(){
		
		return ontologyModel;
	}
}
