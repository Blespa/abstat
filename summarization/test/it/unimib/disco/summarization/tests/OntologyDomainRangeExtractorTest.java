package it.unimib.disco.summarization.tests;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import it.unimib.disco.summarization.datatype.Concept;
import it.unimib.disco.summarization.datatype.Property;
import it.unimib.disco.summarization.relation.OntologyDomainRangeExtractor;

import java.util.ArrayList;

import org.junit.Ignore;
import org.junit.Test;

import com.hp.hpl.jena.ontology.OntProperty;

public class OntologyDomainRangeExtractorTest {

	@Test
	@Ignore
	public void shouldParseAnEmptyOntology() {
		Property properties = new Property();
		properties.setExtractedProperty(new ArrayList<OntProperty>());
		
		OntologyDomainRangeExtractor extractor = new OntologyDomainRangeExtractor();
		extractor.setConceptsDomainRange(new Concept(), properties);
		
		assertThat(extractor.getPropertyDomainRange().getDRRelation().size(), equalTo(0));
	}
}
