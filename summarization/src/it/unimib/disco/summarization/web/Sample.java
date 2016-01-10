package it.unimib.disco.summarization.web;

import com.hp.hpl.jena.query.*;
import java.io.*;

import it.unimib.disco.summarization.dataset.FileSystemConnector;
import it.unimib.disco.summarization.dataset.TextInput;

public class Sample implements Api {
	
	public InputStream get(RequestParameters request) throws Exception{
		String endpoint = request.get("endpoint");
		String akpURI = request.get("akp");
		
		File file = new File("URI-object-akp.txt");
		FileInputStream fis= new FileInputStream(file);
		
		String queryString = null;
		TextInput iteratorFile = new TextInput(new FileSystemConnector(file));
		boolean trovato = false;
		
		while(iteratorFile.hasNextLine() && !trovato){
			String line = iteratorFile.nextLine();
			queryString = buildQuery(line, akpURI, "object");
			if(queryString != null)	
				trovato = true;
		}
		fis.close();
		
		if(!trovato){	
			file = new File("URI-datatype-akp.txt");
			fis= new FileInputStream(file);
			iteratorFile = new TextInput(new FileSystemConnector(file));
			
			while(iteratorFile.hasNextLine() && !trovato){
				String line = iteratorFile.nextLine();
				queryString = buildQuery(line, akpURI, "datatype");
				if(queryString != null)
					trovato = true;
			}
			fis.close();
		}
		
		Query query = QueryFactory.create(queryString);
		QueryExecution qexec = QueryExecutionFactory.sparqlService(endpoint, query);
		ResultSet results = qexec.execSelect();
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();	
		ResultSetFormatter.outputAsJSON(out, results);	
		byte[] data = out.toByteArray();
		ByteArrayInputStream istream = new ByteArrayInputStream(data);
		
		return istream;
		
	}
		
	public String buildQuery(String line, String s, String type) {
		int k = line.indexOf("$");
		String URI = line.substring(0, k);
		
		if(!URI.equals(s))
			return null;
		
		else{
			line = line.substring(k+1);  //elimino uri
			k = line.indexOf("$");
			String sogg = line.substring(0, k);
			line = line.substring(k+1);  //elimino sogg
			k = line.indexOf("$");
			String pred = line.substring(0, k);
			String ogg = line.substring(k+1);  //elimino pred e rimane l'ogg
			String query = null;
			
			if(type.equals("object")){
				query = "SELECT * " +
					    "WHERE {" +
					    "       ?s ?p ?o."+
						"       ?s a <" + sogg + ">." +
						"       ?o a <" + ogg + ">." +
						"       ?s <" + pred + "> ?o." +
						"      }LIMIT 30";
			}
			else if (!ogg.equals("http://www.w3.org/2000/01/rdf-schema#Literal")){
				query = "SELECT * " +
						"WHERE {" +
						"       ?s ?p ?o."+
						"       ?s a <" + sogg + ">." +
						"       ?s <" + pred + "> ?o." +
						"       FILTER(regex(str(datatype(?o)), \"" + ogg + "\"))"+
						"      }LIMIT 30";
			}
			else {
				query = "SELECT * " +
						"WHERE {" +
						"       ?s ?p ?o."+
						"       ?s a <" + sogg + ">." +
						"       ?s <" + pred + "> ?o." +
						"      }LIMIT 30";
			}
			return query;
		}
	}
}