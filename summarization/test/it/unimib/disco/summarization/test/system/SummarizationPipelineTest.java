package it.unimib.disco.summarization.test.system;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;
import it.unimib.disco.summarization.experiments.SparqlEndpoint;
import it.unimib.disco.summarization.export.Events;
import it.unimib.disco.summarization.ontology.LDSummariesVocabulary;

import java.util.HashMap;

import org.junit.Test;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.vocabulary.RDFS;

public class SummarizationPipelineTest {

	@Test
	public void shouldDistinguishBetweenDatatypeAndObjectProperties() {
		Events.summarization();
		
		LDSummariesVocabulary vocabulary = new LDSummariesVocabulary(ModelFactory.createDefaultModel(), "system-test");
		
		String propertiesReferredToFoafName = "select ?property ?occurrence "
											+ "from <" + vocabulary.graph() + "> "
											+ "where { "
											+ "?property <" + RDFS.seeAlso + "> <http://xmlns.com/foaf/0.1/name> . "
											+ "?property <" + vocabulary.occurrence() + "> ?occurrence ." 
											+ " }"; 
		ResultSet results = SparqlEndpoint.local().execute(propertiesReferredToFoafName);
		HashMap<String, Integer> occurrences = new HashMap<String, Integer>();
		while(results.hasNext()){
			QuerySolution solution = results.nextSolution();
			occurrences.put(solution.get("property").toString(), solution.get("occurrence").asLiteral().getInt());
		}
		
		assertThat(occurrences.size(), equalTo(2));
		assertThat(occurrences.get("http://ld-summaries.org/resource/system-test/datatype-property/xmlns.com/foaf/0.1/name"), equalTo(7));
		assertThat(occurrences.get("http://ld-summaries.org/resource/system-test/object-property/xmlns.com/foaf/0.1/name"), equalTo(1));
	}
	
	@Test
	public void theSummaryShouldBeLoadedIntoIntoTheSparqlEndpoint() throws Exception {
		Events.summarization();
		
		LDSummariesVocabulary vocabulary = new LDSummariesVocabulary(ModelFactory.createDefaultModel(), "system-test");
		
		String allTriples = "select (count(*) as ?res) from <" + vocabulary.graph() + "> where {?s ?p ?o}";
		int numberOfTriples = SparqlEndpoint.local().execute(allTriples).nextSolution().get("res").asLiteral().getInt();
		
		assertThat(numberOfTriples, equalTo(4123));
	}
}
