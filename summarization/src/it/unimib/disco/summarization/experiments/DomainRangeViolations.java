package it.unimib.disco.summarization.experiments;

import it.unimib.disco.summarization.ontology.ConceptExtractor;
import it.unimib.disco.summarization.ontology.Concepts;
import it.unimib.disco.summarization.ontology.OntologyDomainRangeExtractor;
import it.unimib.disco.summarization.ontology.OntologySubclassOfExtractor;
import it.unimib.disco.summarization.ontology.Properties;
import it.unimib.disco.summarization.ontology.PropertyExtractor;
import it.unimib.disco.summarization.output.Events;
import it.unimib.disco.summarization.utility.TypeGraph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.vocabulary.OWL;

public class DomainRangeViolations {
	
	public static void main(String[] args) throws Exception {

		new Events();

		String ontology = args[0];
		String dataset = args[1];
		
		Ontology model = new Ontology(ontology);
		TypeGraph types = buildTypeGraph(model.get());
		
		for(OntProperty property : model.properties()){
			Inferred datasetInferences = new Inferred(dataset).of(property.toString());
			signal("POSSIBLE DOMAIN VIOLATION", property, property.getDomain(), datasetInferences.domains(), types);
			signal("POSSIBLE RANGE VIOLATION", property, property.getRange(), datasetInferences.ranges(), types);
		}
	}

	private static void signal(String label, OntProperty property, OntResource declared, HashSet<String> domains, TypeGraph types) {
		if(declared != null && !declared.equals(OWL.Thing) && !declared.isAnon()){
			for(String inferred : domains){
				if(types.pathsBetween(inferred, declared.toString()).isEmpty()){
					System.out.println(label);
					System.out.println(property + " has " +  inferred + " expected " + declared);
				}
			}
		}
	}
	
	private static TypeGraph buildTypeGraph(OntModel ontology) throws Exception {
		PropertyExtractor pExtract = new PropertyExtractor();
		pExtract.setProperty(ontology);
		
		Properties properties = new Properties();
		properties.setProperty(pExtract.getProperty());
		properties.setExtractedProperty(pExtract.getExtractedProperty());
		properties.setCounter(pExtract.getCounter());
		
		ConceptExtractor cExtract = new ConceptExtractor();
		cExtract.setConcepts(ontology);
		
		Concepts concepts = new Concepts();
		concepts.setConcepts(cExtract.getConcepts());
		concepts.setExtractedConcepts(cExtract.getExtractedConcepts());
		concepts.setObtainedBy(cExtract.getObtainedBy());
		
		OntologySubclassOfExtractor extractor = new OntologySubclassOfExtractor();
		extractor.setConceptsSubclassOf(concepts, ontology);
		
		ArrayList<String> subclassRelations = new ArrayList<String>();
		for(List<OntClass> subClasses : extractor.getConceptsSubclassOf().getConceptsSubclassOf()){
			subclassRelations.add(subClasses.get(0) + "##" + subClasses.get(1));
		}
		
		OntologyDomainRangeExtractor DRExtractor = new OntologyDomainRangeExtractor();
		DRExtractor.setConceptsDomainRange(concepts, properties);
		return new TypeGraph(concepts, subclassRelations);
	}
}
