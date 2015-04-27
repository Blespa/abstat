package it.unimib.disco.summarization.tests;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;
import it.unimib.disco.summarization.datatype.Concepts;
import it.unimib.disco.summarization.datatype.EquivalentConcepts;
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
		
		EquivalentConcepts equivalentConcepts = equivalentConceptsFrom(ontology);
		
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
		
		EquivalentConcepts equivalentConcepts = equivalentConceptsFrom(ontology);
		
		assertThat(equivalentConcepts.getExtractedEquConcept().size(), equalTo(1));
	}
	
	private EquivalentConcepts equivalentConceptsFrom(ToyOntology ontology) {
		
		OntModel model = ontology.build();
		
		ConceptExtractor conceptExtractor = new ConceptExtractor();
		conceptExtractor.setConcepts(model);
		
		Concepts concepts = new Concepts();
		concepts.setConcepts(conceptExtractor.getConcepts());
		concepts.setExtractedConcepts(conceptExtractor.getExtractedConcepts());
		concepts.setObtainedBy(conceptExtractor.getObtainedBy());
		
		EqConceptExtractor equConcepts = new EqConceptExtractor();
		equConcepts.setEquConcept(concepts, model);
		
		EquivalentConcepts equConcept = new EquivalentConcepts();
		equConcept.setExtractedEquConcept(equConcepts.getExtractedEquConcept());
		equConcept.setEquConcept(equConcepts.getEquConcept());
		
		return equConcept;
	}
}