package it.unimib.disco.summarization.test.unit;

import java.io.StringWriter;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDFS;

public class ToyOntology{
	
	private OntModel model;
	private Resource lastSubject;
	private Property lastProperty;

	public ToyOntology rdfs(){
		model = ModelFactory.createOntologyModel(OntModelSpec.RDFS_MEM);
		return this;
	}
	
	public ToyOntology owl(){
		model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
		return this;
	}
	
	public ToyOntology definingConcept(String uri){
		lastSubject = model.createClass(uri);
		return this;
	}
	
	public ToyOntology definingResource(String uri){
		lastSubject = model.createResource(uri);
		return this;
	}
	
	public ToyOntology aSubconceptOf(String uri){
		return thatHasProperty(RDFS.subClassOf).linkingTo(uri);
	}
	
	public ToyOntology equivalentTo(String concept) {
		return thatHasProperty(OWL.equivalentClass).linkingTo(concept);
	}
	
	public ToyOntology linkingTo(String uri){
		model.add(lastSubject, lastProperty, model.createResource(uri));
		return this;
	}
	
	public ToyOntology thatHasProperty(Property property){
		lastProperty = property;
		return this;
	}
	
	public OntModel build(){
		return model;
	}
	
	public String serialize(){
		StringWriter result = new StringWriter();
		build().write(result);
		return result.toString();
	}
}