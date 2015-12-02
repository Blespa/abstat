package it.unimib.disco.summarization.experiments;

import it.unimib.disco.summarization.ontology.LDSummariesVocabulary;
import it.unimib.disco.summarization.ontology.TypeOf;

import java.util.List;

import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;

public class DomainsAndRanges {

	public static void main(String[] args) {
		String dataset = "dbpedia-2014";
		String domain = "dbpedia.org/ontology";
		
		LDSummariesVocabulary vocabulary = new LDSummariesVocabulary(ModelFactory.createDefaultModel(), dataset);
		SparqlEndpoint endpoint = SparqlEndpoint.abstat();
		TypeOf classifier = new TypeOf(domain);
		
		List<Resource[]> allProperties = new SummarizedProperties(vocabulary, endpoint).all();
		
		for(Resource[] property : allProperties){
			String uri = property[1].toString();
			String type = classifier.resource(property[0].toString());
			
			System.out.println(escaped(uri) + "\t" + escaped(type));
		}
	}

	private static String escaped(String uri) {
		return "\"" + uri + "\"";
	}
}
