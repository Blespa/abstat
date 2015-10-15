package it.unimib.disco.summarization.experiments;

import it.unimib.disco.summarization.ontology.LDSummariesVocabulary;

import java.util.HashSet;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.vocabulary.RDFS;

public class Inferred{
	
	private String dataset;
	private HashSet<String> domains;
	private HashSet<String> ranges;

	public Inferred(String dataset){
		this.dataset = dataset;
		this.domains = new HashSet<String>();
		this.ranges = new HashSet<String>();
	}
	
	public Inferred of(String property){
		LDSummariesVocabulary vocabulary = new LDSummariesVocabulary(ModelFactory.createDefaultModel(), dataset);
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

		ResultSet patterns = SparqlEndpoint.abstat().execute(query);
		while(patterns.hasNext()){
			QuerySolution solution = patterns.nextSolution();
			String subject = solution.get("subject").toString();
			String object = solution.get("object").toString();
			if(!isExternal(subject)){
				domains.add(subject);
			}
			if(!isExternal(object)){
				ranges.add(object);
			}
		}
		return this;
	}
	
	public HashSet<String> domains(){
		return new HashSet<String>(domains);
	}
	
	public HashSet<String> ranges(){
		return new HashSet<String>(ranges);
	}
	
	private boolean isExternal(String resource) {
		return resource.contains("wikidata.dbpedia.org") || 
			   resource.contains("ontologydesignpatterns") || 
			   resource.contains("schema.org") ||
			   resource.contains("xmlns.com/foaf/") ||
			   resource.contains("Wikidata:") ||
			   resource.contains("www.opengis.net") ||
			   resource.contains("/ontology/bibo/");
	}
}