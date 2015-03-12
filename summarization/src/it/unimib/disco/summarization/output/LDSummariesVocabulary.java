package it.unimib.disco.summarization.output;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

public class LDSummariesVocabulary {

	private Model model;

	public LDSummariesVocabulary(Model model) {
		this.model = model;
	}

	public Resource akpConcept() {
		return model.createResource(ontologyNamespace() + "AbstractKnowledgePattern");
	}

	public Property frequency() {
		return model.createProperty(ontologyNamespace() + "instanceOccurrence");
	}

	private String ontologyNamespace() {
		return "http://schemasummaries.org/ontology/";
	}
}
