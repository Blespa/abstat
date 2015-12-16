package it.unimib.disco.summarization.experiments;

import it.unimib.disco.summarization.export.Events;
import it.unimib.disco.summarization.ontology.LDSummariesVocabulary;
import it.unimib.disco.summarization.ontology.TypeOf;

import java.util.HashMap;

import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;

public class DatatypeAndObjectProperties {
	
	public static void main(String[] args) throws Exception {
		Events.summarization();
		
		String dataset = args[0];
		String domainName = args[1];
		String ontologyPath = args[2];
		
		TypeOf classifier = new TypeOf(domainName);
		LDSummariesVocabulary vocabulary = new LDSummariesVocabulary(ModelFactory.createDefaultModel(), dataset);
		SparqlEndpoint endpoint = SparqlEndpoint.abstat();
		
		HashMap<String, OntProperty> properties = ontologyProperties(ontologyPath);
		
		for(Resource[] summarizedProperty : new SummarizedProperties(vocabulary, endpoint).all()){
			Resource datasetProperty = summarizedProperty[0];
			Resource ontologyProperty = summarizedProperty[1];
			
			if(classifier.resource(datasetProperty.getURI()).equals("external")) continue;
			if(properties.get(ontologyProperty.toString()).isDatatypeProperty() && datasetProperty.getURI().contains("object-property")){
				System.out.println(datasetProperty + " is declared as datatype property, but used as object property");
			}
			
			if(properties.get(ontologyProperty.toString()).isObjectProperty() && datasetProperty.getURI().contains("datatype-property")){
				System.out.println(datasetProperty + " is declared as object property, but used as datatype property");
			}
		}
	}

	private static HashMap<String, OntProperty> ontologyProperties(String ontologyPath) {
		BenchmarkOntology ontology = new BenchmarkOntology(ontologyPath);
		HashMap<String, OntProperty> properties = new HashMap<String, OntProperty>();
		for(OntProperty property : ontology.properties()){
			properties.put(property.toString(), property);
		}
		return properties;
	}
}
