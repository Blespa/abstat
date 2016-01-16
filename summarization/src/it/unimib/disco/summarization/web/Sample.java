package it.unimib.disco.summarization.web;

import com.hp.hpl.jena.query.*;
import java.io.*;
import it.unimib.disco.summarization.experiments.SparqlEndpoint;

public class Sample implements Api {
	
	public InputStream get(RequestParameters request) throws Exception{
		String endpoint = request.get("endpoint");
		String akpURI = request.get("akp");
		
		String[] tripla_info = getAKP(akpURI);
		String queryString = buildQuery(tripla_info);
		
		Query query = QueryFactory.create(queryString);
		QueryExecution qexec = QueryExecutionFactory.sparqlService(endpoint, query);
		ResultSet results = qexec.execSelect();
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();	
		ResultSetFormatter.outputAsJSON(out, results);
		byte[] data = out.toByteArray();
		ByteArrayInputStream istream = new ByteArrayInputStream(data);
		
		return istream;
	}
	
	public static String[] getAKP(String akpURI) {
		SparqlEndpoint localEndpoint = SparqlEndpoint.local();
		ResultSet results;
		int i, l, j, k;
		String[] array = new String[3];
		String[] tripla = new String[4];  //conterrà sogg, pred, ogg e property_type
		String property_type = "";
		
		
		results = localEndpoint.execute("DESCRIBE <" + akpURI + ">");
		String result = ResultSetFormatter.asText(results); 
		
		
		i = result.indexOf("<http://www.w3.org/1999/02/22-rdf-syntax-ns#subject>");
		l = "<http://www.w3.org/1999/02/22-rdf-syntax-ns#subject>".length();
		String temp = result.substring(i+l);
		j = temp.indexOf('<'); k = temp.indexOf('>');
		array[0] = temp.substring(j, k+1);
		
		i = result.indexOf("<http://www.w3.org/1999/02/22-rdf-syntax-ns#predicate>");
		l = "<http://www.w3.org/1999/02/22-rdf-syntax-ns#predicate>".length();
		temp = result.substring(i+l);
		j = temp.indexOf('<'); k = temp.indexOf('>');
		array[1] = temp.substring(j, k+1);
		
		i = result.indexOf("<http://www.w3.org/1999/02/22-rdf-syntax-ns#object>");
		l = "<http://www.w3.org/1999/02/22-rdf-syntax-ns#object>".length();
		temp = result.substring(i+l);
		j = temp.indexOf('<'); k = temp.indexOf('>');
		array[2] = temp.substring(j, k+1);
		
		
		if (array[2].contains("/www.w3.org/2000/01/rdf-schema#Literal"))
			property_type = "datatype-property_Literal";
		else if ( array[1].contains("datatype-property"))
			property_type = "datatype";
		else
			property_type = "object-property";
		tripla[3] = property_type;
		
		
		
		results = localEndpoint.execute("SELECT ?o " +
									 	 "WHERE { "   +
									 	 array[0] + " <http://www.w3.org/2000/01/rdf-schema#seeAlso> ?o" +
										 "}");
		String res = ResultSetFormatter.asText(results);
		j = res.indexOf('<'); k = res.indexOf('>');
		tripla[0] = res.substring(j+1, k);
		
		results = localEndpoint.execute("SELECT ?o " +
									 	 "WHERE { "   +
									 	 array[1] + " <http://www.w3.org/2000/01/rdf-schema#seeAlso> ?o" +
										 "}");
		res = ResultSetFormatter.asText(results);
		j = res.indexOf('<'); k = res.indexOf('>');
		tripla[1] = res.substring(j+1, k);
		
		results = localEndpoint.execute("SELECT ?o " +
			 	 						"WHERE { "   +
			 	 						array[2] + " <http://www.w3.org/2000/01/rdf-schema#seeAlso> ?o" +
				 						"}");
		res = ResultSetFormatter.asText(results);
		j = res.indexOf('<'); k = res.indexOf('>');
		tripla[2] = res.substring(j+1, k);
	
		return tripla;
	}
	
	
		
	public String buildQuery(String[] tripla_info) {
		String query;
		if(tripla_info[3].equals("object-property")){
			query = "SELECT DISTINCT ?s ?p ?o " +
				    "WHERE {" +
					"       ?s a <" + tripla_info[0] + ">." +
					"       ?o a <" + tripla_info[2] + ">." +
					"       ?s <" + tripla_info[1] + "> ?o." +
					"       BIND( <"+ tripla_info[1] + "> AS ?p)." +
					"      }LIMIT 30";
		}
		else if (!tripla_info[3].equals("datatype-property_Literal")){
			query = "SELECT DISTINCT ?s ?p ?o " +
					"WHERE {" +
					"       ?s a <" + tripla_info[0] + ">." +
					"       ?s <" + tripla_info[1] + "> ?o." +
					"       BIND( <"+ tripla_info[1] + "> AS ?p)." +
					"       FILTER(regex(str(datatype(?o)), \"" + tripla_info[2] + "\"))"+
					"      }LIMIT 30";
		}
		else {
			query = "SELECT DISTINCT ?s ?p ?o " +
					"WHERE {" +
					"       ?s a <" + tripla_info[0] + ">." +
					"       ?s <" + tripla_info[1] + "> ?o." +
					"       BIND( <"+ tripla_info[1] + "> AS ?p)." +
					"      }LIMIT 30";
		}
		return query;
	}
}