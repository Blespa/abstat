package it.unimib.disco.summarization.experiments;

import it.unimib.disco.summarization.ontology.Model;
import it.unimib.disco.summarization.ontology.PropertyExtractor;

import java.io.File;
import java.util.List;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;

public class BenchmarkOntology{
	
	private OntModel ontology;

	public BenchmarkOntology(String path){
		String file = new File("../benchmark/experiments/" + path).getAbsolutePath().replace("summarization/../", "");
		ontology = new Model(file, "RDF/XML").getOntologyModel();
	}
	
	public List<OntProperty> properties() {
		return new PropertyExtractor().setProperty(ontology).getExtractedProperty();
	}
	
	public OntModel get(){
		return ontology;
	}
}