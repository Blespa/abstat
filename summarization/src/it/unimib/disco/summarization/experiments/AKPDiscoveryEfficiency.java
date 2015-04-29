package it.unimib.disco.summarization.experiments;

import it.unimib.disco.summarization.output.Events;
import it.unimib.disco.summarization.output.LDSummariesVocabulary;

import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDFS;

public class AKPDiscoveryEfficiency {

	public static void main(String[] args) {
		
//		String dataset = "dbpedia2014";
//		String ontology = "filter regex(?p, 'http://dbpedia.org/ontology')";
//		String datasetGraph = "from <http://dbpedia.org>";
		String dataset = "linked-brainz";
		String ontology = "";
		String datasetGraph = "";
		
		new Events();
		
		LDSummariesVocabulary vocabulary = new LDSummariesVocabulary(ModelFactory.createDefaultModel(), dataset);
		
		String mostFrequentPropertiesAndObjects = "select ?type ?occurrences "
				+ "from <" + vocabulary.graph() + "> "
				+ "where {"
				+ "?localDatatype a <" + vocabulary.type() + "> ."
				+ "?localDatatype <" + vocabulary.occurrence() + "> ?occurrences . "
				+ "?localDatatype <" + RDFS.seeAlso + "> ?type . "
				+ "filter not exists{"
				+ "  ?localDatatype a <" + vocabulary.datatype() + "> . "
				+ "}"
				+ "filter regex(?type, '" + ontology + "')"
				+ "filter (!regex(?type, 'Wikidata'))"
				+ "} "
				+ "order by desc(?occurrences) limit 20";

		ResultSet result = SparqlEndpoint.abstat().execute(mostFrequentPropertiesAndObjects);
		while(result.hasNext()){
			Resource concept = result.next().getResource("?type");
			System.out.println("processing " + concept);
			String abstatPatterns = "select ?predicate ?object ?occurrence "
								+ "from <" + vocabulary.graph() + "> "
								+ "where {"
									+ "?pattern a <" + vocabulary.abstractKnowledgePattern() + "> ."
									+ "?pattern <" + vocabulary.subject() + "> ?localSubject ."
									+ "?localSubject <" + RDFS.seeAlso + "> <" + concept + "> ."
									+ "?pattern <" + vocabulary.occurrence() + "> ?occurrence ."
									+ "?pattern <" + vocabulary.predicate() + "> ?localPredicate ."
									+ "?localPredicate <" + RDFS.seeAlso + "> ?predicate ."
									+ "?pattern <" + vocabulary.object() + "> ?localObject ."
									+ "?localObject <" + RDFS.seeAlso + "> ?object ."
								+ "} order by desc(?occurrence)";
			
			SparqlEndpoint.abstat().execute(abstatPatterns);
			
			String datasetPatterns = "select ?p ?o count(?p) count(?o) "
									+ datasetGraph + " "
									+ "where {"
										+ "?instance a <" + concept + "> ."
										+ "?instance ?p ?o ."
										+ ontology + " "
									+ "} group by ?p ?o";
			
			SparqlEndpoint.dataset(dataset).execute(datasetPatterns);
			System.out.println("done");
		}
	}
}
