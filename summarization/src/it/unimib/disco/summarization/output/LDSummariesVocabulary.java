package it.unimib.disco.summarization.output;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

public class LDSummariesVocabulary {

	private Model model;
	private String dataset;

	public LDSummariesVocabulary(Model model, String dataset) {
		this.model = model;
		this.dataset = new RDFResource(dataset).localName();
	}

	public Resource akpConcept() {
		return model.createResource(ontologyNamespace() + "AbstractKnowledgePattern");
	}
	
	public Resource aakpConcept() {
		return model.createResource(ontologyNamespace() + "AggregatedAbstractKnowledgePattern");
	}

	public Resource type() {
		return model.createResource(ontologyNamespace() + "Type");
	}
	
	public Resource datatype() {
		return model.createResource(ontologyNamespace() + "Datatype");
	}
	
	public Property occurrences() {
		return model.createProperty(ontologyNamespace() + "instanceOccurrence");
	}
	
	public Property minTypeSubOccurrence() {
		return model.createProperty(ontologyNamespace() + "minTypeSubOccurrence");
	}
	
	public Property minTypeObjOccurrence() {
		return model.createProperty(ontologyNamespace() + "minTypeObjOccurrence");
	}
	
	public Resource akpInstance(String... elements) {
		return aggregate("AKP", elements);
	}
	
	public Resource aakpInstance(String... elements) {
		return aggregate("AAKP", elements);
	}

	private Resource aggregate(String type, String... elements) {
		List<String> localNames = new ArrayList<String>();
		for(String element : elements){
			localNames.add(new RDFResource(element).localName());
		}
		
		return model.createResource(resourcesNamespace() + dataset + "/" + type + "_" + StringUtils.join(localNames, "_"));
	}

	private String ontologyNamespace() {
		return baseUri() + "ontology/";
	}

	private String resourcesNamespace() {
		return baseUri() + "resource/";
	}

	private String baseUri() {
		return "http://schemasummaries.org/";
	}
}
