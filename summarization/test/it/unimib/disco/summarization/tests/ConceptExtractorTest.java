package it.unimib.disco.summarization.tests;

import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import it.unimib.disco.summarization.extraction.ConceptExtractor;

import java.util.HashMap;

import org.junit.Test;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDFS;

public class ConceptExtractorTest {

	@Test
	public void shouldSpotAOWLClearDefinedConcept() {
		
		OntModel model = ModelFactory.createOntologyModel(OntModelSpec.RDFS_MEM);
		model.createClass("http://the.class");
		
		HashMap<String, String> concepts = conceptsFrom(model);
		
		assertThat(concepts.get("http://the.class"), notNullValue());
	}
	
	@Test
	public void shouldSpotAlsoImplicitTypeDeclarations() throws Exception {
		
		OntModel model = ModelFactory.createOntologyModel(OntModelSpec.RDFS_MEM);
		Resource father = model.createResource("http://father");
		Resource parent = model.createResource("http://parent");
		
		model.add(father, RDFS.subClassOf, parent);
		
		HashMap<String, String> concepts = conceptsFrom(model);
		
		assertThat(concepts.get("http://father"), notNullValue());
		assertThat(concepts.get("http://parent"), notNullValue());
	}
	
	private HashMap<String, String> conceptsFrom(OntModel model) {
		
		ConceptExtractor conceptExtractor = new ConceptExtractor();
		conceptExtractor.setConcepts(model);
		
		return conceptExtractor.getConcepts();
	}

}
