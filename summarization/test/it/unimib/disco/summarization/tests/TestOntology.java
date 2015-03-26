package it.unimib.disco.summarization.tests;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDFS;

public class TestOntology{
	
	private OntModel model;
	private Resource lastSubject;
	private Property lastProperty;

	public TestOntology rdfs(){
		model = ModelFactory.createOntologyModel(OntModelSpec.RDFS_MEM);
		return this;
	}
	
	public TestOntology owl(){
		model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
		return this;
	}
	
	public TestOntology definingConcept(String uri){
		lastSubject = model.createClass(uri);
		return this;
	}
	
	public TestOntology definingResource(String uri){
		lastSubject = model.createResource(uri);
		return this;
	}
	
	public TestOntology aSubconceptOf(String uri){
		return thatHasProperty(RDFS.subClassOf).linkingTo(uri);
	}
	
	public TestOntology linkingTo(String uri){
		model.add(lastSubject, lastProperty, model.createResource(uri));
		return this;
	}
	
	public TestOntology thatHasProperty(Property property){
		lastProperty = property;
		return this;
	}
	
	public OntModel build(){
		return model;
	}
}