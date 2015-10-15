package it.unimib.disco.summarization.experiments;

import it.unimib.disco.summarization.export.Events;
import it.unimib.disco.summarization.ontology.ConceptExtractor;
import it.unimib.disco.summarization.ontology.Concepts;
import it.unimib.disco.summarization.ontology.OntologyDomainRangeExtractor;
import it.unimib.disco.summarization.ontology.OntologySubclassOfExtractor;
import it.unimib.disco.summarization.ontology.Properties;
import it.unimib.disco.summarization.ontology.PropertyExtractor;
import it.unimib.disco.summarization.ontology.TypeGraph;

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

		Events.summarization();

		String ontology = args[0];
		String dataset = args[1];
		
		BenchmarkOntology model = new BenchmarkOntology(ontology);
		TypeGraph types = buildTypeGraph(model.get());
		
		HashSet<String> domainsViolations = new HashSet<String>();
		HashSet<String> rangesViolations = new HashSet<String>();
		
		for(OntProperty property : model.properties()){
			
			Inferred datasetInferences = new Inferred(dataset).of(property.toString());
			
			OntResource domain = property.getDomain();
			OntResource range = property.getRange();
			
			List<String> domainViolations = violations(types, domain, datasetInferences.domains());
			List<String> rangeViolations = violations(types, range, datasetInferences.ranges());
			
			if(!domainViolations.isEmpty()) domainsViolations.add(property.toString());
			if(!rangeViolations.isEmpty()) rangesViolations.add(property.toString());
			
			if(!domainViolations.isEmpty()  || !rangeViolations.isEmpty()){
				System.out.println("------------------------------------------");
				System.out.println(domain + " - " + property + " - " + range);
				System.out.println("----- DOMAIN VIOLATIONS\n" + domainViolations);
				System.out.println("----- RANGE VIOLATIONS\n" + rangeViolations);
			}
		}
		
		HashSet<String> violations = new HashSet<String>();
		violations.addAll(domainsViolations);
		violations.addAll(rangesViolations);
		
		System.out.println("------------------------------------------");
		System.out.println("----- Total domain / range violations: " + violations.size());
		System.out.println(violations);
		System.out.println("----- Total domain violations: " + domainsViolations.size());
		System.out.println(domainsViolations);
		System.out.println("----- Total range violations: " + rangesViolations.size());
		System.out.println(rangesViolations);
	}

	private static List<String> violations(TypeGraph types, OntResource declared, HashSet<String> domains) {
		List<String> domainViolations = new ArrayList<String>();
		if(declared != null && !declared.equals(OWL.Thing) && !declared.isAnon()){
			for(String inferred : domains){
				if(inferred.equals(OWL.Thing.toString()) || inferred.equals(declared.toString())) continue;
				if(!types.pathsBetween(declared.toString(), inferred).isEmpty()){
					domainViolations.add(inferred);
				}
			}
		}
		return domainViolations;
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
