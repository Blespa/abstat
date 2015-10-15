package it.unimib.disco.summarization.experiments;

import it.unimib.disco.summarization.export.Events;
import it.unimib.disco.summarization.ontology.LDSummariesVocabulary;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.joda.time.DateTime;
import org.joda.time.Interval;

import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

public class AKPDiscoveryEfficiency {

	public static void main(String[] args) {

		String dataset = args[0];
		String ontology = "";
		String ontologyFilter = "";
		String datasetGraph = "";
		
		if(dataset.equals("dbpedia2014")){
			ontology = "http://dbpedia.org/ontology";
			ontologyFilter = "filter regex(?p, 'http://dbpedia.org/ontology')";
			datasetGraph = "from <http://dbpedia.org>";
		}
		
		Events.summarization();
		
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
		
		DescriptiveStatistics abstatResponseTimes = new DescriptiveStatistics();
		DescriptiveStatistics datasetResponseTimes = new DescriptiveStatistics();
		int totalConcepts = 0;
		
		while(result.hasNext()){
			Resource concept = result.next().getResource("?type");
			totalConcepts++;
			
			Events.summarization().info("processing " + concept);
			
			try{
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
				
				DateTime start = DateTime.now();
				SparqlEndpoint.abstat().execute(abstatPatterns);
				DateTime end = DateTime.now();
				
				long duration = new Interval(start, end).toDurationMillis();
				abstatResponseTimes.addValue(duration);
				
			}catch(Exception e){
				Events.summarization().error(concept, e);
			}
			
			try{
				String datasetPatterns = "select ?p ?objectType count(?p) count(?objectType) "
										+ datasetGraph + " "
										+ "where {"
											+ "?instance a <" + concept + "> ."
											+ "?instance ?p ?o . "
											+ "?o <" + RDF.type + "> ?objectType ."
											+ ontologyFilter + " "
										+ "} group by ?p ?objectType";
				
				DateTime start = DateTime.now();
				SparqlEndpoint.dataset(dataset).execute(datasetPatterns);
				DateTime end = DateTime.now();
				
				long duration = new Interval(start, end).toDurationMillis();
				datasetResponseTimes.addValue(duration);
				
			}catch(Exception e){
				Events.summarization().error(concept, e);
			}
		}
		
		long abstatTimeouts = totalConcepts -abstatResponseTimes.getN();
		long datasetTimeouts = totalConcepts - datasetResponseTimes.getN();
		System.out.println("ABSTAT average response time: " + abstatResponseTimes.getMean() + " - for " + totalConcepts +" concepts (" + abstatTimeouts + " timeouts)");
		System.out.println(dataset + " average response time: " + datasetResponseTimes.getMean() + " - for " + totalConcepts +" concepts (" + datasetTimeouts + " timeouts)");
	}
}
