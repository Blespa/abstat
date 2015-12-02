package it.unimib.disco.summarization.experiments;

import it.unimib.disco.summarization.ontology.LDSummariesVocabulary;

import java.util.HashSet;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.vocabulary.RDFS;

public class Inferred{
	
	private HashSet<String> domains;
	private HashSet<String> ranges;
	private LDSummariesVocabulary vocabulary;
	private SparqlEndpoint endpoint;

	public Inferred(LDSummariesVocabulary vocabulary, SparqlEndpoint endpoint){
		this.vocabulary = vocabulary;
		this.endpoint = endpoint;
		this.domains = new HashSet<String>();
		this.ranges = new HashSet<String>();
	}
	
	public Inferred of(String property){
		String query = "select ?subject ?object "
					 + "from <" + vocabulary.graph() + "> "
			 		 + "where {"
			 		 	+ "?pattern a <"+ vocabulary.abstractKnowledgePattern().getURI() + "> ."
			 		 	+ "?pattern <"+ vocabulary.predicate() + "> ?predicate ."
			 		 	+ "?predicate <"+ RDFS.seeAlso + "> <" + property + "> ."
			 		 	+ "?pattern <"+ vocabulary.subject()+ "> ?localSubject ."
			 		 	+ "?localSubject <"+ RDFS.seeAlso + "> ?subject ."
			 		 	+ "?pattern <"+ vocabulary.object()+ "> ?localObject ."
			 		 	+ "?localObject <"+ RDFS.seeAlso + "> ?object ."
			 		 + "} order by ?subject";

		ResultSet patterns = endpoint.execute(query);
		while(patterns.hasNext()){
			QuerySolution solution = patterns.nextSolution();
			String subject = solution.get("subject").toString();
			String object = solution.get("object").toString();
			domains.add(subject);
			ranges.add(object);
		}
		return this;
	}
	
	public HashSet<String> domains(){
		return new HashSet<String>(domains);
	}
	
	public HashSet<String> ranges(){
		return new HashSet<String>(ranges);
	}
}