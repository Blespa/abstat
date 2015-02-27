package it.unimib.disco.summarization.utility;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;

/**
 * Instantiate Ontology Or Dataset Model
 */
public class Model {
	
	OntModel ontologyModel;
	//TODO: Creare modello del dataset
	
	public Model(String Spec, String OwlBaseFile, String FileType){
		setOntologyModel(Spec, OwlBaseFile, FileType);
	}
	
	public void setOntologyModel(String Spec, String OwlBaseFile, String FileType){
		
		//TODO: Determinare parametro di OntModelSpec in base al file in input o in base alle necessit� di REASONING - Usare Spec
		  //OWL_DL_MEM_RULE_INF permette di ottenere inferenza sulle relazioni
		
		ontologyModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM, null); 
		
		//TODO: Determinare secondo parametro in base al file in input
		ontologyModel.read(OwlBaseFile, FileType); 
	}
	
	public OntModel getOntologyModel(){
		
		return ontologyModel;
	}
}
