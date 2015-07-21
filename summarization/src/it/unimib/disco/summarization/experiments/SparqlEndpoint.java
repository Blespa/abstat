package it.unimib.disco.summarization.experiments;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.Syntax;

public class SparqlEndpoint{
	
	public static SparqlEndpoint local(){
		return new SparqlEndpoint("http://localhost");
	}
	
	public static SparqlEndpoint abstatBackend(){
		return new SparqlEndpoint("http://193.204.59.21:8885/");
	}
	
	public static SparqlEndpoint abstat(){
		return new SparqlEndpoint("http://abstat.disco.unimib.it");
	}
	
	public static SparqlEndpoint dataset(String dataset){
		if(dataset.equals("dbpedia2014")) return new SparqlEndpoint("http://dbpedia.org");
		return new SparqlEndpoint("http://linkedbrainz.org/");
	}
	
	private String host;

	private SparqlEndpoint(String host){
		this.host = host;
	}
	
	public ResultSet execute(String query) {
		Query jenaQuery = QueryFactory.create(query, Syntax.syntaxARQ);
		QueryExecution sparqlService = QueryExecutionFactory.sparqlService(host + "/sparql", jenaQuery);
//		sparqlService.setTimeout(20000);
		return sparqlService.execSelect();
	}
}