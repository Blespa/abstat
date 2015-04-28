package it.unimib.disco.summarization.experiments;

import it.unimib.disco.summarization.ontology.PropertyExtractor;
import it.unimib.disco.summarization.output.Events;
import it.unimib.disco.summarization.utility.Model;

import java.io.File;
import java.util.List;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.OntResource;

public class ComputeUnderspecifiedPropertiesStatistics {

	public static void main(String[] args) {
		
		new Events();
		
		String path = "music-ontology/mo.owl";
		String prefix = "http://purl.org/ontology/mo/";
		
		String file = new File("../benchmark/experiments/" + path).getAbsolutePath().replace("summarization/../", "");
		OntModel ontology = new Model(file,"RDF/XML").getOntologyModel();
		
		List<OntProperty> properties = new PropertyExtractor().setProperty(ontology).getExtractedProperty();
		
		for(OntProperty property : properties){
			OntResource range = property.getRange();
			OntResource domain = property.getDomain();
			
			if(!property.getURI().contains(prefix)) continue;
			if(domain != null && range != null) continue;
			
			System.out.println(domain + " - " + property + " - " + range);
		}
	}

}
