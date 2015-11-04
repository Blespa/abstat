package it.unimib.disco.summarization.ontology;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

public class LDSummariesVocabulary {

	private Model model;
	private String dataset;

	public LDSummariesVocabulary(Model model, String dataset) {
		this.model = model;
		this.dataset = new RDFResource(dataset).localName();
	}

	public Resource selfOrUntyped(String concept) {
		if(concept.equals("Ukn_Type")) {
			return RDFS.Literal;
		}
		return model.createResource(concept);
	}
	
	public Resource abstractKnowledgePattern() {
		return model.createResource(ontologyNamespace() + "AbstractKnowledgePattern");
	}
	
	public Resource aggregatePattern() {
		return model.createResource(ontologyNamespace() + "AggregatePattern");
	}

	public Resource property() {
		return model.createResource(ontologyNamespace() + "Property");
	}
	
	public Resource type() {
		return model.createResource(ontologyNamespace() + "Type");
	}
	
	public Resource datatype() {
		return model.createResource(ontologyNamespace() + "Datatype");
	}
	
	public Resource external() {
		return model.createResource(ontologyNamespace() + "External");
	}
	
	public Resource internal() {
		return model.createResource(ontologyNamespace() + "Internal");
	}
	
	public Resource concept() {
		return model.createResource(skos() + "Concept");
	}
	
	public Property broader() {
		return model.createProperty(skos() + "broader");
	}

	private String skos() {
		return "http://www.w3.org/2004/02/skos/core#";
	}
	
	public Property subject() {
		return RDF.subject;
	}
	
	public Property object() {
		return RDF.object;
	}
	
	public Property predicate() {
		return RDF.predicate;
	}
	
	public Property occurrence() {
		return model.createProperty(ontologyNamespace() + "occurrence");
	}
	
	public Property subjectOccurrence() {
		return model.createProperty(ontologyNamespace() + "subjectOccurrence");
	}
	
	public Property objectOccurrence() {
		return model.createProperty(ontologyNamespace() + "objectOccurrence");
	}
	
	public Property subjectMinTypes() {
		return model.createProperty(ontologyNamespace() + "subjectMinTypes");
	}
	
	public Property objectMinTypes() {
		return model.createProperty(ontologyNamespace() + "objectMinTypes");
	}
	
	public Resource akpInstance(String... elements) {
		return aggregate("AKP", elements);
	}
	
	public Resource aakpInstance(String... elements) {
		return aggregate("AP", elements);
	}
	
	public Resource asLocalResource(String globalResource) {
		return model.createResource(resourcesNamespace() + asLocal(globalResource));
	}

	public Resource asLocalDatatypeProperty(String globalProperty) {
		return model.createResource(datatypePropertiesNameSpace() + asLocal(globalProperty));
	}
	
	public Resource asLocalObjectProperty(String globalProperty) {
		return model.createResource(objectPropertiesNameSpace() + asLocal(globalProperty));
	}
	
	public Resource addConcept(String concept) {
		
		Resource globalSubject = model.createResource(concept);
		Resource localSubject = asLocalResource(globalSubject.getURI());
		
		model.add(localSubject, RDFS.seeAlso, globalSubject);
		
		model.add(localSubject, RDF.type, type());
		model.add(localSubject, RDF.type, concept());
		return localSubject;
	}
	
	public String graph(){
		return baseUri() + dataset;
	}

	private Resource aggregate(String type, String... elements) {
		List<String> localNames = new ArrayList<String>();
		for(String element : elements){
			localNames.add(element);
		}
		
		return model.createResource(resourcesNamespace() + type + "/" + DigestUtils.md5Hex(StringUtils.join(localNames, "")));
	}

	private String ontologyNamespace() {
		return baseUri() + "ontology/";
	}

	private String resourcesNamespace() {
		return baseUri() + "resource/" + dataset + "/";
	}
	
	private String datatypePropertiesNameSpace(){
		return resourcesNamespace() + "datatype-property/";
	}
	
	private String objectPropertiesNameSpace(){
		return resourcesNamespace() + "object-property/";
	}
	
	private String asLocal(String globalResource) {
		return globalResource.replace("http://", "");
	}

	private String baseUri() {
		return "http://ld-summaries.org/";
	}
}
