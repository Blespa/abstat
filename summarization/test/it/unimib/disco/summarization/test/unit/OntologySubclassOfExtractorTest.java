package it.unimib.disco.summarization.test.unit;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import it.unimib.disco.summarization.ontology.ConceptExtractor;
import it.unimib.disco.summarization.ontology.Concepts;
import it.unimib.disco.summarization.ontology.OntologySubclassOfExtractor;
import it.unimib.disco.summarization.ontology.SubClassOf;

import java.util.List;

import org.junit.Test;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;

public class OntologySubclassOfExtractorTest {

	@Test
	public void shouldExtractTheHierarchyFromCanonicalOWL() {
		
		ToyOntology model = new ToyOntology()
				.owl()
				.definingConcept("http://father")
				.aSubconceptOf("http://parent");
		
		assertArePresent(subClassesFrom(model), "http://father", "http://parent");
	}
	
	@Test
	public void shouldExtractTheHierarchyInPresenceOfMultipleInheritance() throws Exception {
		
		ToyOntology model = new ToyOntology()
										.rdfs()
										.definingConcept("http://father")
										.aSubconceptOf("http://parent")
										.definingConcept("http://mother")
										.aSubconceptOf("http://parent");
		
		SubClassOf subClasses = subClassesFrom(model);
		
		assertArePresent(subClasses, "http://mother", "http://parent");
	}

	private void assertArePresent(SubClassOf subClasses, String son, String father) {
		for(List<OntClass> relations : subClasses.getConceptsSubclassOf()){
			if(relations.get(0).getURI().equals(son)){
				assertThat(relations.get(1).getURI(), equalTo(father));
				return;
			}
		}
		fail();
	}

	private SubClassOf subClassesFrom(ToyOntology ontology) {
		
		OntModel model = ontology.build();
		
		ConceptExtractor conceptExtractor = new ConceptExtractor();
		conceptExtractor.setConcepts(model);
		
		Concepts concepts = new Concepts();
		concepts.setConcepts(conceptExtractor.getConcepts());
		concepts.setExtractedConcepts(conceptExtractor.getExtractedConcepts());
		concepts.setObtainedBy(conceptExtractor.getObtainedBy());
		
		OntologySubclassOfExtractor extractor = new OntologySubclassOfExtractor();
		extractor.setConceptsSubclassOf(concepts, model);
		return extractor.getConceptsSubclassOf();
	}
}
