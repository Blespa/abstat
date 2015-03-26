package it.unimib.disco.summarization.tests;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import it.unimib.disco.summarization.datatype.Concept;
import it.unimib.disco.summarization.datatype.DomainRange;
import it.unimib.disco.summarization.datatype.Property;
import it.unimib.disco.summarization.extraction.ConceptExtractor;
import it.unimib.disco.summarization.extraction.PropertyExtractor;
import it.unimib.disco.summarization.relation.OntologyDomainRangeExtractor;

import org.junit.Test;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.vocabulary.RDFS;

public class OntologyDomainRangeExtractorTest {

	@Test
	public void shouldParseASimpleOntology() throws Exception {
		
		ToyOntology model = new ToyOntology()
				.owl()
				.definingResource("http://livesIn")
					.thatHasProperty(RDFS.domain)
						.linkingTo("http://actor")
					.thatHasProperty(RDFS.range)
						.linkingTo("http://city");
		
		DomainRange patterns = patternsFrom(model.build());
		
		assertThat(patterns.getDRRelation().get("http://livesIn").get(0).getURI(), equalTo("http://actor"));
		assertThat(patterns.getDRRelation().get("http://livesIn").get(1).getURI(), equalTo("http://city"));
	}

	private DomainRange patternsFrom(OntModel model) {
		
		PropertyExtractor propertyExtractor = new PropertyExtractor();
		propertyExtractor.setProperty(model);
		Property properties = new Property();
		properties.setExtractedProperty(propertyExtractor.getExtractedProperty());
		
		ConceptExtractor conceptExtractor = new ConceptExtractor();
		conceptExtractor.setConcepts(model);
		
		Concept concepts = new Concept();
		concepts.setConcepts(conceptExtractor.getConcepts());
		concepts.setExtractedConcepts(conceptExtractor.getExtractedConcepts());
		concepts.setObtainedBy(conceptExtractor.getObtainedBy());
		OntologyDomainRangeExtractor extractor = new OntologyDomainRangeExtractor();
		extractor.setConceptsDomainRange(concepts, properties);
		
		DomainRange patterns = extractor.getPropertyDomainRange();
		return patterns;
	}
}
