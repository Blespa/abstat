package it.unimib.disco.summarization.experiments;

import it.unimib.disco.summarization.ontology.PropertyExtractor;
import it.unimib.disco.summarization.output.Events;
import it.unimib.disco.summarization.output.LDSummariesVocabulary;
import it.unimib.disco.summarization.utility.Model;

import java.io.File;
import java.util.HashSet;
import java.util.List;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.vocabulary.RDFS;

public class ComputeUnderspecifiedPropertiesStatistics {

	public static void main(String[] args) {

		new Events();

		String path = args[0];
		String dataset = args[1];
		
		LDSummariesVocabulary vocabulary = new LDSummariesVocabulary(ModelFactory.createDefaultModel(), dataset);

		for (OntProperty property : propertiesOf(path)) {
			OntResource range = property.getRange();
			OntResource domain = property.getDomain();
			
			if(range != null && domain != null) continue;
			
			String query = "select ?subject ?object "
						 + "from <" + vocabulary.graph() + "> "
				 		 + "where {"
				 		 	+ "?pattern a <"+ vocabulary.abstractKnowledgePattern().getURI() + "> ."
				 		 	+ "?pattern <"+ vocabulary.predicate() + "> ?predicate ."
				 		 	+ "?predicate <"+ RDFS.seeAlso + "> <" + property + "> ."
				 		 	+ "?pattern <"+ vocabulary.subject()+ "> ?localSubject ."
				 		 	+ "?localSubject <"+ RDFS.seeAlso + "> ?subject ."
				 		 	+ "?pattern <"+ vocabulary.object()+ "> ?localObject ."
				 		 	+ "?localObject <"+ RDFS.seeAlso + "> ?object ."
				 		 + "} order by ?subject";

			ResultSet patterns = select(query);
			HashSet<String> inferredDomain = new HashSet<String>();
			HashSet<String> inferredRange = new HashSet<String>();
			while(patterns.hasNext()){
				QuerySolution solution = patterns.nextSolution();
				String subject = solution.get("subject").toString();
				String object = solution.get("object").toString();
				if(!isExternal(subject)){
					inferredDomain.add(subject);
				}
				if(!isExternal(object)){
					inferredRange.add(object);
				}
			}
			if(!inferredDomain.isEmpty()) {
				System.out.println("------------------------------------------");
				System.out.println(domain + " - " + property + " - " + range);
				System.out.println("----- DOMAINS\n" + inferredDomain);
				System.out.println("----- RANGES\n" + inferredRange);
			}
		}
	}

	private static boolean isExternal(String resource) {
		return resource.contains("wikidata.dbpedia.org") || 
			   resource.contains("ontologydesignpatterns") || 
			   resource.contains("schema.org") ||
			   resource.contains("xmlns.com/foaf/") ||
			   resource.contains("Wikidata:") ||
			   resource.contains("www.opengis.net") ||
			   resource.contains("/ontology/bibo/");
	}

	private static ResultSet select(String query) {
		return QueryExecutionFactory.sparqlService("http://abstat.disco.unimib.it:8890/sparql", 
											 	   QueryFactory.create(query))
							 .execSelect();
	}

	private static List<OntProperty> propertiesOf(String path) {
		String file = new File("../benchmark/experiments/" + path).getAbsolutePath().replace("summarization/../", "");
		OntModel ontology = new Model(file, "RDF/XML").getOntologyModel();
		return new PropertyExtractor().setProperty(ontology).getExtractedProperty();
	}

}
