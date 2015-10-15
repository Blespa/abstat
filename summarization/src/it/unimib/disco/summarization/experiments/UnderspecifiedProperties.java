package it.unimib.disco.summarization.experiments;

import it.unimib.disco.summarization.export.Events;

import java.util.HashSet;

import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.OntResource;

public class UnderspecifiedProperties {

	public static void main(String[] args) {

		Events.summarization();

		String path = args[0];
		String dataset = args[1];
		
		HashSet<String> underspecifiedPropertyDomains = new HashSet<String>();
		HashSet<String> underspecifiedPropertyRanges = new HashSet<String>();
		
		for (OntProperty property : new BenchmarkOntology(path).properties()) {
			OntResource range = property.getRange();
			OntResource domain = property.getDomain();
			
			if(range != null && domain != null) {
				continue;
			}
			
			if(domain == null ) underspecifiedPropertyDomains.add(property.toString());
			if(range == null ) underspecifiedPropertyRanges.add(property.toString());
			
			Inferred inferred = new Inferred(dataset).of(property.toString());
			
			HashSet<String> domains = inferred.domains();
			HashSet<String> ranges = inferred.ranges();
			
			if(!domains.isEmpty() && !ranges.isEmpty()) {
				System.out.println("------------------------------------------");
				System.out.println(domain + " - " + property + " - " + range);
				System.out.println("----- DOMAINS\n" + domains);
				System.out.println("----- RANGES\n" + ranges);
			}
		}
		
		HashSet<String> underSpecifiedProperties = new HashSet<String>();
		underSpecifiedProperties.addAll(underspecifiedPropertyDomains);
		underSpecifiedProperties.addAll(underspecifiedPropertyRanges);
		
		System.out.println("------------------------------------------");
		System.out.println("----- Total underspecified properties: " + underSpecifiedProperties.size());
		System.out.println(underSpecifiedProperties);
		System.out.println("----- Total underspecified domains: " + underspecifiedPropertyDomains.size());
		System.out.println(underspecifiedPropertyDomains);
		System.out.println("----- Total underspecified ranges: " + underspecifiedPropertyRanges.size());
		System.out.println(underspecifiedPropertyRanges);
	}
}
