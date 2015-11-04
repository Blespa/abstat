package it.unimib.disco.summarization.ontology;

import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;

public class RDFTypeOf {

	private TypeOf type;
	private LDSummariesVocabulary voc;

	public RDFTypeOf(String domain) {
		this.type = new TypeOf(domain);
		this.voc = new LDSummariesVocabulary(ModelFactory.createDefaultModel(), "any");
	}

	public Resource resource(String resource) {
		if(type.resource(resource).equals("external")) return voc.external();
		return voc.internal();
	}

	public Resource objectAKP(String subject, String object) {
		if(type.objectAKP(subject, object).equals("external")) return voc.external();
		return voc.internal();
	}

	public Resource datatypeAKP(String subject) {
		if(type.datatypeAKP(subject).equals("external")) return voc.external();
		return voc.internal();
	}

}
