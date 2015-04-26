package it.unimib.disco.summarization.utility;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;

/**
 * Instantiate Ontology Or Dataset Model
 */
public class Model {
	
	OntModel ontologyModel;
	
	public Model(String Spec, String OwlBaseFile, String FileType){
		setOntologyModel(Spec, OwlBaseFile, FileType);
	}
	
	public void setOntologyModel(String Spec, String OwlBaseFile, String FileType){
		
		ontologyModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM, null); 
		ontologyModel.read(OwlBaseFile, FileType); 
	}
	
	public OntModel getOntologyModel(){
		
		return ontologyModel;
	}
}
