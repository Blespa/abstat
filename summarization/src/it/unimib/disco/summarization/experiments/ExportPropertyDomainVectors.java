package it.unimib.disco.summarization.experiments;

import it.unimib.disco.summarization.output.LDSummariesVocabulary;
import it.unimib.disco.summarization.utility.BulkTextOutput;
import it.unimib.disco.summarization.utility.FileSystemConnector;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDFS;

public class ExportPropertyDomainVectors {

	public static void main(String[] args) throws Exception {
		final File directory = new File(args[0]);
		final String dataset = args[1];
		
		final LDSummariesVocabulary vocabulary = new LDSummariesVocabulary(ModelFactory.createDefaultModel(), dataset);
		final Property subject = vocabulary.subject();
		
		final List<Resource> properties = allProperties(vocabulary);
		
		ExecutorService executor = Executors.newFixedThreadPool(10);
		for(final Resource property : properties){
			executor.execute(new Runnable() {
				@Override
				public void run() {
					try {
						exportProperty(directory, dataset, vocabulary, subject, property);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
		}
		executor.shutdown();
	    while(!executor.isTerminated()){}
	}

	private static void exportProperty(File directory, String dataset,
			LDSummariesVocabulary vocabulary, Property subject,
			Resource property) throws Exception {
		String vector = "select ?type ?typeOcc ?propOcc (sum(?occ) as ?akpOcc) where {" +
						   "<"+ property +"> <" + vocabulary.occurrence() + "> ?propOcc ." +
						   "?akp <" + vocabulary.predicate() + "> <"+ property +"> . " +
						   "?akp <" + subject + "> ?ls . " +
						   "?ls <" + RDFS.seeAlso + "> ?type . " +
						   "?ls <" + vocabulary.occurrence() + "> ?typeOcc . " +
						   "?akp <" + vocabulary.occurrence() + "> ?occ . " +
						    "} group by ?type ?typeOcc ?propOcc order by ?type";
		
		ResultSet v = SparqlEndpoint.abstatBackend().execute(vector);
		
		BulkTextOutput out = new BulkTextOutput(new FileSystemConnector(new File(directory,
																					property.toString()
																					.replace("http://ld-summaries.org/resource/", "")
																					.replace(dataset, "")
																					.replace("/datatype-property/", "")
																					.replace("/object-property/", "")
																					.replace("/", "_"))), 20);
		if(!v.hasNext()) return;
		while(v.hasNext()){
			QuerySolution result = v.next();
			Resource type = result.getResource("?type");
			Literal typeOcc = result.getLiteral("?typeOcc");
			Literal propOcc = result.getLiteral("?propOcc");
			Literal akpOcc = result.getLiteral("?akpOcc");
			
			out.writeLine(type + "|" + typeOcc.getLong() + "|" + propOcc.getLong() + "|" + akpOcc.getLong());
		}
		out.close();
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
