package it.unimib.disco.summarization.experiments;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.Syntax;

class SparqlEndpoint{
	
	public static SparqlEndpoint abstat(){
		return new SparqlEndpoint("http://abstat.disco.unimib.it:8890");
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