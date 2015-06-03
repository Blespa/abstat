package it.unimib.disco.summarization.systemTests;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import it.unimib.disco.summarization.experiments.SparqlEndpoint;
import it.unimib.disco.summarization.output.Events;
import it.unimib.disco.summarization.output.LDSummariesVocabulary;

import java.util.HashMap;

import org.junit.Ignore;
import org.junit.Test;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.vocabulary.RDFS;

public class SummarizationPipelineTest {

	@Test
	@Ignore
	public void shouldDistinguishBetweenDatatypeAndObjectProperties() {
		new Events();
		
		LDSummariesVocabulary vocabulary = new LDSummariesVocabulary(ModelFactory.createDefaultModel(), "system-test");
		
		String propertiesReferredToFoafName = "select ?property ?occurrence "
											+ "from <" + vocabulary.graph() + "> "
											+ "where { "
											+ "?property <" + RDFS.seeAlso + "> <http://xmlns.com/foaf/0.1/name> . "
											+ "?property <" + vocabulary.occurrence() + "> ?occurrence ." 
											+ " }"; 
		ResultSet results = SparqlEndpoint.local().execute(propertiesReferredToFoafName);
		HashMap<String, String> occurrences = new HashMap<String, String>();
		while(results.hasNext()){
			QuerySolution solution = results.nextSolution();
			occurrences.put(solution.get("property").toString(), solution.get("occurrence").toString());
		}
		
		assertThat(occurrences.size(), equalTo(2));
		assertThat(occurrences.get("http://ld-summaries.org/resource/datatype-property/system-test/xmlns.com/foaf/0.1/name"), equalTo("1"));
		assertThat(occurrences.get("http://ld-summaries.org/resource/object-property/system-test/xmlns.com/foaf/0.1/name"), equalTo("1"));
	}
}
