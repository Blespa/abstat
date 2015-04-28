package it.unimib.disco.summarization.experiments;

import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.Syntax;

class AbstatEndpoint{
	
	public ResultSet execute(String query) {
		return QueryExecutionFactory.sparqlService("http://abstat.disco.unimib.it:8890/sparql", 
											 	   QueryFactory.create(query, Syntax.syntaxARQ))
							 .execSelect();
	}
}