package it.unimib.disco.summarization.tests;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import it.unimib.disco.summarization.datatype.Concept;
import it.unimib.disco.summarization.datatype.SubClassOf;
import it.unimib.disco.summarization.extraction.ConceptExtractor;
import it.unimib.disco.summarization.relation.OntologySubclassOfExtractor;

import java.util.List;

import org.junit.Test;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class OntologySubclassOfExtractorTest {

	@Test
	public void shouldExtractTheHierarchyFromCanonicalOWL() {
		OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
		
		OntClass concept = model.createClass("http://father");
		concept.addSuperClass(model.createResource("http://parent"));
		
		assertArePresent(subClassesFrom(model), concept.getURI(), "http://parent");
	}
	
	@Test
	public void shouldExtractTheHierarchyInPresenceOfMultipleInheritance() throws Exception {
		OntModel model = ModelFactory.createOntologyModel(OntModelSpec.RDFS_MEM);
		
		OntClass father = model.createClass("http://father");
		father.addSuperClass(model.createResource("http://parent"));
		
		OntClass mother = model.createClass("http://mother");
		mother.addSuperClass(model.createResource("http://parent"));

		SubClassOf subClasses = subClassesFrom(model);
		
		assertArePresent(subClasses, mother.getURI(), "http://parent");
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

	private SubClassOf subClassesFrom(OntModel model) {
		
		ConceptExtractor conceptExtractor = new ConceptExtractor();
		conceptExtractor.setConcepts(model);
		
		Concept concepts = new Concept();
		concepts.setConcepts(conceptExtractor.getConcepts());
		concepts.setExtractedConcepts(conceptExtractor.getExtractedConcepts());
		concepts.setObtainedBy(conceptExtractor.getObtainedBy());
		
		OntologySubclassOfExtractor extractor = new OntologySubclassOfExtractor();
		extractor.setConceptsSubclassOf(concepts, model);
		return extractor.getConceptsSubclassOf();
	}
}
