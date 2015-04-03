package it.unimib.disco.summarization.tests;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;
import it.unimib.disco.summarization.datatype.Concept;
import it.unimib.disco.summarization.datatype.EquConcept;
import it.unimib.disco.summarization.extraction.ConceptExtractor;
import it.unimib.disco.summarization.extraction.EqConceptExtractor;

import org.junit.Test;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.vocabulary.OWL;

public class EqConceptExtractorTest {

	@Test
	public void shouldSpotASimpleEquivalence() {
		
		ToyOntology ontology = new ToyOntology()
									.owl()
									.definingConcept("http://schema.org/Place")
									.definingConcept("http://dbpedia.org/Place")
										.thatHasProperty(OWL.equivalentClass)
										.linkingTo("http://schema.org/Place");
		
		EquConcept equivalentConcepts = equivalentConceptsFrom(ontology);
		
		assertThat(equivalentConcepts.getExtractedEquConcept().size(), equalTo(1));
	}
	
	@Test
	public void shouldSpotAnEquivalenceAlsoOnBadFormattedURI() throws Exception {
		
		ToyOntology ontology = new ToyOntology()
										.rdfs()
										.definingConcept("http://any")
										.definingConcept("http://mpii.de/yago/resource/wikicategory_Dams_in_Washington_D.C.")
											.thatHasProperty(OWL.equivalentClass)
											.linkingTo("http://any");
		
		EquConcept equivalentConcepts = equivalentConceptsFrom(ontology);
		
		assertThat(equivalentConcepts.getExtractedEquConcept().size(), equalTo(1));
	}
	
	private EquConcept equivalentConceptsFrom(ToyOntology ontology) {
		
		OntModel model = ontology.build();
		
		ConceptExtractor conceptExtractor = new ConceptExtractor();
		conceptExtractor.setConcepts(model);
		
		Concept concepts = new Concept();
		concepts.setConcepts(conceptExtractor.getConcepts());
		concepts.setExtractedConcepts(conceptExtractor.getExtractedConcepts());
		concepts.setObtainedBy(conceptExtractor.getObtainedBy());
		
		EqConceptExtractor equConcepts = new EqConceptExtractor();
		equConcepts.setEquConcept(concepts, model);
		
		EquConcept equConcept = new EquConcept();
		equConcept.setExtractedEquConcept(equConcepts.getExtractedEquConcept());
		equConcept.setEquConcept(equConcepts.getEquConcept());
		
		return equConcept;
	}
}
