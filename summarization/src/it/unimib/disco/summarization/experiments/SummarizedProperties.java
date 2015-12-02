package it.unimib.disco.summarization.experiments;

import it.unimib.disco.summarization.ontology.LDSummariesVocabulary;

import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDFS;

public class SummarizedProperties{
	
	private LDSummariesVocabulary vocabulary;
	private SparqlEndpoint endpoint;

	public SummarizedProperties(LDSummariesVocabulary vocabulary,SparqlEndpoint endpoint) {
		this.vocabulary = vocabulary;
		this.endpoint = endpoint;
	}

	public List<Resource[]> all() {
		String allProperties = "select distinct ?property ?uri from <" + vocabulary.graph() + "> " + 
								"where { " +
								"?property a <" + vocabulary.property() + "> . " +
								"?property <" + RDFS.seeAlso + "> ?uri ." +
								"}";
		ResultSet allPropertiesResults = endpoint.execute(allProperties);
		List<Resource[]> properties = new ArrayList<Resource[]>();
		while(allPropertiesResults.hasNext()){
			QuerySolution next = allPropertiesResults.next();
			properties.add(new Resource[]{
					next.getResource("?property"),
					next.getResource("?uri")
			});
		}
		return properties;
	}
}