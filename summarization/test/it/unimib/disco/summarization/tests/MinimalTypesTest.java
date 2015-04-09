package it.unimib.disco.summarization.tests;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import it.unimib.disco.summarization.datatype.Concept;
import it.unimib.disco.summarization.extraction.ConceptExtractor;
import it.unimib.disco.summarization.relation.OntologySubclassOfExtractor;
import it.unimib.disco.summarization.utility.MinimalTypes;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.semanticweb.yars.nx.namespace.OWL;

import com.hp.hpl.jena.ontology.OntClass;

public class MinimalTypesTest extends TestWithTemporaryData{

	@Test
	public void shouldParseAnEmptyTripleFile() throws Exception {
		
		ToyOntology ontology = new ToyOntology().owl();
		
		File types = temporary.namedFile("", "s_types.nt");
		File directory = temporary.directory();
		
		new MinimalTypes(getConceptsFrom(ontology), writeSubClassRelationsFrom(ontology)).computeFor(types, directory);
		
		assertThat(new File(directory, "s_minType.txt").exists(), is(true));
		assertThat(new File(directory, "s_uknHierConcept.txt").exists(), is(true));
		assertThat(new File(directory, "s_countConcepts.txt").exists(), is(true));
	}
	
	@Test
	public void shouldSkipOwlThing() throws Exception {
		
		ToyOntology ontology = new ToyOntology().owl();
		
		File types = temporary.namedFile("", "s_types.nt");
		File directory = temporary.directory();
		
		new MinimalTypes(getConceptsFrom(ontology), writeSubClassRelationsFrom(ontology)).computeFor(types, directory);
		
		assertThat(new File(directory, "s_minType.txt").exists(), is(true));
		assertThat(new File(directory, "s_uknHierConcept.txt").exists(), is(true));
		assertThat(new File(directory, "s_countConcepts.txt").exists(), is(true));
	}
	
	@Test
	public void shouldCountConceptsEvenWhenTheyHaveNoInstance() throws Exception {
		ToyOntology ontology = new ToyOntology().owl();
		
		File types = temporary.namedFile("<http://entity> <> <" + OWL.THING + "> .", "s_types.nt");
		File directory = temporary.directory();
		
		new MinimalTypes(getConceptsFrom(ontology), writeSubClassRelationsFrom(ontology)).computeFor(types, directory);
		
		assertThat(linesOf("s_minType.txt"), hasSize(0));
		assertThat(linesOf("s_uknHierConcept.txt"), hasSize(0));
		assertThat(linesOf("s_countConcepts.txt"), hasSize(0));
	}
	
	@Test
	public void shouldCountTheRightNumberOfConcepts() throws Exception {
		ToyOntology ontology = new ToyOntology()
								.owl()
								.definingConcept("http://concept");

		File types = temporary.namedFile("<http://entity> <> <http://concept> .", "s_types.nt");
		File directory = temporary.directory();
		
		new MinimalTypes(getConceptsFrom(ontology), writeSubClassRelationsFrom(ontology)).computeFor(types, directory);
		List<String> conceptCounts = linesOf("s_countConcepts.txt");
		
		assertThat(conceptCounts, hasItem("http://concept##1"));
	}
	
	@Test
	public void shouldCountAlsoUnknownConcepts() throws Exception {
		ToyOntology ontology = new ToyOntology().owl();

		File types = temporary.namedFile("<http://entity> <> <http://concept> .", "s_types.nt");
		File directory = temporary.directory();
		
		new MinimalTypes(getConceptsFrom(ontology), writeSubClassRelationsFrom(ontology)).computeFor(types, directory);
		
		assertThat(linesOf("s_countConcepts.txt"), is(empty()));
		assertThat(linesOf("s_uknHierConcept.txt"), hasItem("http://entity##http://concept"));
	}
	
	@Test
	public void shouldTrackASimpleMinimalType() throws Exception {
		ToyOntology ontology = new ToyOntology()
										.owl()
										.definingConcept("http://concept");

		File types = temporary.namedFile("<http://entity> <> <http://concept> .", "s_types.nt");
		File directory = temporary.directory();
		
		new MinimalTypes(getConceptsFrom(ontology), writeSubClassRelationsFrom(ontology)).computeFor(types, directory);
		
		assertThat(linesOf("s_minType.txt"), hasItem("http://entity##http://concept"));
	}
	
	private List<String> linesOf(String name) throws IOException {
		return FileUtils.readLines(new File(temporary.directory(), name));
	}
	
	private Concept getConceptsFrom(ToyOntology ontology){
		
		ConceptExtractor conceptExtractor = new ConceptExtractor();
		conceptExtractor.setConcepts(ontology.build());
		
		Concept concepts = new Concept();
		concepts.setConcepts(conceptExtractor.getConcepts());
		concepts.setExtractedConcepts(conceptExtractor.getExtractedConcepts());
		concepts.setObtainedBy(conceptExtractor.getObtainedBy());
		
		return concepts;
	}
	
	private File writeSubClassRelationsFrom(ToyOntology ontology) throws Exception{
		
		OntologySubclassOfExtractor extractor = new OntologySubclassOfExtractor();
		extractor.setConceptsSubclassOf(getConceptsFrom(ontology), ontology.build());
		
		List<String> result = new ArrayList<String>();
		for(List<OntClass> subClasses : extractor.getConceptsSubclassOf().getConceptsSubclassOf()){
			result.add(subClasses.get(0) + "##" + subClasses.get(1));
		}
		
		return temporary.file(StringUtils.join(result, "\n"));
	}
}
