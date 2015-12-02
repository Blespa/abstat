package it.unimib.disco.summarization.experiments;

import it.unimib.disco.summarization.ontology.LDSummariesVocabulary;

import java.util.List;

import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;

public class DomainsAndRanges {

	public static void main(String[] args) {
		String dataset = "dbpedia-2014";
		
		LDSummariesVocabulary vocabulary = new LDSummariesVocabulary(ModelFactory.createDefaultModel(), dataset);
		SparqlEndpoint endpoint = SparqlEndpoint.abstat();
		
		List<Resource[]> allProperties = new SummarizedProperties(vocabulary, endpoint).all();
		
		for(Resource[] property : allProperties){
			System.out.println("\"" + property[1] + "\"");
		}
	}
}
