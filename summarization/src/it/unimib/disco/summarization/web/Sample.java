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
		
		SparqlEndpoint  externalEndpoint = SparqlEndpoint.external(endpoint);
		ResultSet results = externalEndpoint.execute(queryString);
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();	
		ResultSetFormatter.outputAsJSON(out, results);
		byte[] data = out.toByteArray();
		ByteArrayInputStream istream = new ByteArrayInputStream(data);
		
		return istream;
	}
	
	private String[] getAKP(String akpURI) {
		SparqlEndpoint localEndpoint = SparqlEndpoint.local();
		ResultSet results;
		String[] tripla_info = new String[4];  //conterrà sogg, pred, ogg e property_type
		String property_type = "";
		
		String query = ("SELECT ?s ?p ?o ?predicate " +
						"WHERE { " +
						"		<" + akpURI + ">" + 
						"						<http://www.w3.org/1999/02/22-rdf-syntax-ns#subject> ?subject;" + 
						"						<http://www.w3.org/1999/02/22-rdf-syntax-ns#predicate> ?predicate;" +
						"						<http://www.w3.org/1999/02/22-rdf-syntax-ns#object> ?object." + 
						"		?subject <http://www.w3.org/2000/01/rdf-schema#seeAlso> ?s." + 
						"		?predicate <http://www.w3.org/2000/01/rdf-schema#seeAlso> ?p." +
						"		?object <http://www.w3.org/2000/01/rdf-schema#seeAlso> ?o." +
						"		}");
		
		results = localEndpoint.execute(query);
		QuerySolution solution = results.nextSolution();
		tripla_info[0] = solution.get("s").toString();
		tripla_info[1] = solution.get("p").toString();
		tripla_info[2] = solution.get("o").toString();
		tripla_info[3] = solution.get("predicate").toString();
		
		if (tripla_info[2].contains("/www.w3.org/2000/01/rdf-schema#Literal"))
			property_type = "datatype-property_Literal";
		else if (tripla_info[3].contains("datatype-property"))
			property_type = "datatype-property";
		else
			property_type = "object-property";
		
		tripla_info[3] = property_type;
		
		return tripla_info;
	}
	
		
	private String buildQuery(String[] tripla_info) {
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