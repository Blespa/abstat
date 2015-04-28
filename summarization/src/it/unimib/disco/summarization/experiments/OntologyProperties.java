package it.unimib.disco.summarization.experiments;

import it.unimib.disco.summarization.ontology.PropertyExtractor;
import it.unimib.disco.summarization.utility.Model;

import java.io.File;
import java.util.List;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;

class OntologyProperties{
	public List<OntProperty> of(String path) {
		String file = new File("../benchmark/experiments/" + path).getAbsolutePath().replace("summarization/../", "");
		OntModel ontology = new Model(file, "RDF/XML").getOntologyModel();
		return new PropertyExtractor().setProperty(ontology).getExtractedProperty();
	}
}