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

	public Resource type() {
		return model.createResource(ontologyNamespace() + "Type");
	}
	
	public Property frequency() {
		return model.createProperty(ontologyNamespace() + "instanceOccurrence");
	}
	
	public Resource akpInstance(String... elements) {
		List<String> localNames = new ArrayList<String>();
		for(String element : elements){
			localNames.add(new RDFResource(element).localName());
		}
	
		return model.createResource(resourcesNamespace() + dataset + "/AKP_" + StringUtils.join(localNames, "_"));
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
