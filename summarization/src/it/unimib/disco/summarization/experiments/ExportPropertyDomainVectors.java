package it.unimib.disco.summarization.experiments;

import it.unimib.disco.summarization.output.LDSummariesVocabulary;
import it.unimib.disco.summarization.utility.BulkTextOutput;
import it.unimib.disco.summarization.utility.FileSystemConnector;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDFS;

public class ExportPropertyDomainVectors {

	public static void main(String[] args) throws Exception {
		File directory = new File(args[0]);
		String dataset = args[1];
		
		LDSummariesVocabulary vocabulary = new LDSummariesVocabulary(ModelFactory.createDefaultModel(), dataset);
		Property subject = vocabulary.subject();
		
		List<Resource> properties = allProperties(vocabulary);
		
		int count = 0;
		for(Resource property : properties){
			count++;
			System.out.println(property + " (" + count + " of " + properties.size() + ")");
			String vector = "select ?type ?typeOcc (sum(?occ) as ?akpOcc) where {" +
							   "?akp <" + vocabulary.predicate() + "> <"+ property +"> . " +
							   "?akp <" + subject + "> ?ls . " +
							   "?ls <" + RDFS.seeAlso + "> ?type . " +
							   "?ls <" + vocabulary.occurrence() + "> ?typeOcc . " +
							   "?akp <" + vocabulary.occurrence() + "> ?occ . " +
							    "} group by ?type ?typeOcc order by ?type";
			
			ResultSet v = SparqlEndpoint.abstatBackend().execute(vector);
			
			BulkTextOutput out = new BulkTextOutput(new FileSystemConnector(new File(directory,
																						property.toString()
																						.replace("http://ld-summaries.org/resource/", "")
																						.replace(dataset, "")
																						.replace("/datatype-property/", "")
																						.replace("/object-property/", "")
																						.replace("/", "_"))), 20);
			while(v.hasNext()){
				QuerySolution result = v.next();
				Resource type = result.getResource("?type");
				Literal typeOcc = result.getLiteral("?typeOcc");
				Literal akpOcc = result.getLiteral("?akpOcc");
				
				out.writeLine(type + "|" + typeOcc.getLong() + "|" + akpOcc.getLong());
			}
			out.close();
		}
	}

	private static List<Resource> allProperties(LDSummariesVocabulary vocabulary) {
		String allProperties = "select distinct ?property from <" + vocabulary.graph() + "> " + 
								"where { " +
								"?property a <" + vocabulary.property() + "> . " +
								"}";
		ResultSet allPropertiesResults = SparqlEndpoint.abstatBackend().execute(allProperties);
		List<Resource> properties = new ArrayList<Resource>();
		while(allPropertiesResults.hasNext()){
			properties.add(allPropertiesResults.next().getResource("?property"));
		}
		return properties;
	}
}
