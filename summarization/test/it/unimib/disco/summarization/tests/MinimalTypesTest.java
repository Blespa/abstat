package it.unimib.disco.summarization.tests;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import it.unimib.disco.summarization.datatype.Concepts;
import it.unimib.disco.summarization.datatype.EquivalentConcepts;
import it.unimib.disco.summarization.extraction.ConceptExtractor;
import it.unimib.disco.summarization.extraction.EqConceptExtractor;
import it.unimib.disco.summarization.relation.OntologySubclassOfExtractor;
import it.unimib.disco.summarization.utility.MinimalTypes;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.vocabulary.OWL;

public class MinimalTypesTest extends TestWithTemporaryData{

	@Test
	public void shouldParseFileNamesWithStrangeSeparators() throws Exception {
		
		ToyOntology ontology = new ToyOntology().owl();
		
		File types = temporary.namedFile("", "__types.nt");
		File directory = temporary.directory();
		
		minimalTypesFrom(ontology).computeFor(types, directory);
		
		assertThat(new File(directory, "__minType.txt").exists(), is(true));
	}
	
	@Test
	public void shouldParseFileNamesThatAreMoreThanOneCharLong() throws Exception {
		
		ToyOntology ontology = new ToyOntology().owl();
		
		File types = temporary.namedFile("", "others_types.nt");
		File directory = temporary.directory();
		
		minimalTypesFrom(ontology).computeFor(types, directory);
		
		assertThat(new File(directory, "others_minType.txt").exists(), is(true));
	}
	
	@Test
	public void shouldParseAnEmptyTripleFile() throws Exception {
		
		ToyOntology ontology = new ToyOntology().owl();
		
		File types = temporary.namedFile("", "s_types.nt");
		File directory = temporary.directory();
		
		minimalTypesFrom(ontology).computeFor(types, directory);
		
		assertThat(new File(directory, "s_minType.txt").exists(), is(true));
		assertThat(new File(directory, "s_uknHierConcept.txt").exists(), is(true));
		assertThat(new File(directory, "s_countConcepts.txt").exists(), is(true));
	}

	@Test
	public void shouldSkipOwlThing() throws Exception {
		
		ToyOntology ontology = new ToyOntology().owl();
		
		File types = temporary.namedFile("", "s_types.nt");
		File directory = temporary.directory();
		
		minimalTypesFrom(ontology).computeFor(types, directory);
		
		assertThat(new File(directory, "s_minType.txt").exists(), is(true));
		assertThat(new File(directory, "s_uknHierConcept.txt").exists(), is(true));
		assertThat(new File(directory, "s_countConcepts.txt").exists(), is(true));
	}
	
	@Test
	public void shouldCountConceptsEvenWhenTheyHaveNoInstance() throws Exception {
		ToyOntology ontology = new ToyOntology().owl();
		
		File types = temporary.namedFile("http://entity##type##" + OWL.Thing, "s_types.nt");
		File directory = temporary.directory();
		
		minimalTypesFrom(ontology).computeFor(types, directory);
		
		assertThat(linesOf("s_minType.txt"), hasSize(0));
		assertThat(linesOf("s_uknHierConcept.txt"), hasSize(0));
		assertThat(linesOf("s_countConcepts.txt"), hasSize(0));
	}
	
	@Test
	public void shouldCountTheRightNumberOfConcepts() throws Exception {
		ToyOntology ontology = new ToyOntology()
								.owl()
								.definingConcept("http://concept");

		File types = temporary.namedFile("http://entity##type##http://concept", "s_types.nt");
		File directory = temporary.directory();
		
		minimalTypesFrom(ontology).computeFor(types, directory);
		List<String> conceptCounts = linesOf("s_countConcepts.txt");
		
		assertThat(conceptCounts, hasItem("http://concept##1"));
	}
	
	@Test
	public void shouldCountAlsoUnknownConcepts() throws Exception {
		ToyOntology ontology = new ToyOntology().owl();

		File types = temporary.namedFile("http://entity##type##http://concept", "s_types.nt");
		File directory = temporary.directory();
		
		minimalTypesFrom(ontology).computeFor(types, directory);
		
		assertThat(linesOf("s_countConcepts.txt"), is(empty()));
		assertThat(linesOf("s_uknHierConcept.txt"), hasItem("http://entity##http://concept"));
	}
	
	@Test
	public void shouldTrackASimpleMinimalType() throws Exception {
		ToyOntology ontology = new ToyOntology()
										.owl()
										.definingConcept("http://concept");

		File types = temporary.namedFile("http://entity##type##http://concept", "s_types.nt");
		File directory = temporary.directory();
		
		minimalTypesFrom(ontology).computeFor(types, directory);
		
		assertThat(linesOf("s_minType.txt"), hasItem("1##http://entity##http://concept"));
	}
	
	@Test
	public void shouldExcludeNonMinimalTypes() throws Exception {
		ToyOntology ontology = new ToyOntology()
										.owl()
										.definingConcept("http://thing")
										.definingConcept("http://concept")
											.aSubconceptOf("http://thing");

		File types = temporary.namedFile("http://entity##type##http://concept"
										+ "\n"
										+ "http://entity##type##http://thing", "s_types.nt");
		File directory = temporary.directory();
		
		minimalTypesFrom(ontology).computeFor(types, directory);
		
		assertThat(linesOf("s_minType.txt"), hasItem("1##http://entity##http://concept"));
	}
	
	@Test
	public void shouldReplaceANonMinimalTypeWhenAMinimalIsFound() throws Exception {
		ToyOntology ontology = new ToyOntology()
										.owl()
										.definingConcept("http://thing")
										.definingConcept("http://concept")
											.aSubconceptOf("http://thing");

		File types = temporary.namedFile("http://entity##type##http://thing"
										+ "\n"
										+ "http://entity##type##http://concept", "s_types.nt");
		File directory = temporary.directory();
		
		minimalTypesFrom(ontology).computeFor(types, directory);
		
		assertThat(linesOf("s_minType.txt"), hasItem("1##http://entity##http://concept"));
	}
	
	@Test
	public void manyMinimalTypes() throws Exception {
		ToyOntology ontology = new ToyOntology()
									.owl()
									.definingConcept("http://thing")
									.definingConcept("http://concept");

		File types = temporary.namedFile("http://entity##type##http://thing"
										+ "\n"
										+ "http://entity##type##http://concept", "s_types.nt");
		File directory = temporary.directory();
		
		minimalTypesFrom(ontology).computeFor(types, directory);
		
		assertThat(linesOf("s_minType.txt"), hasItem("2##http://entity##http://concept##http://thing"));
	}
	
	@Test
	public void equivalentExternalConcept() throws Exception {
		ToyOntology ontology = new ToyOntology()
										.owl()
										.definingConcept("http://dbpedia.org/Car")
											.equivalentTo("http://schema.org/Car");
		
		File types = temporary.namedFile("http://entity##type##http://schema.org/Car", "s_types.nt");
		File directory = temporary.directory();
		
		minimalTypesFrom(ontology).computeFor(types, directory);
		
		assertThat(linesOf("s_minType.txt"), hasItem("1##http://entity##http://schema.org/Car"));		
	}
	
	@Test
	public void equivalentConcept() throws Exception {
		ToyOntology ontology = new ToyOntology()
										.owl()
										.definingConcept("http://dbpedia.org/Car")
											.equivalentTo("http://schema.org/Car");
		
		File types = temporary.namedFile("http://entity##type##http://dbpedia.org/Car", "s_types.nt");
		File directory = temporary.directory();
		
		minimalTypesFrom(ontology).computeFor(types, directory);
		
		assertThat(linesOf("s_minType.txt"), hasItem("1##http://entity##http://dbpedia.org/Car"));		
	}
	
	@Test
	public void equivalentConceptInTheSameHierarchy() throws Exception {
		ToyOntology ontology = new ToyOntology()
										.owl()
										.definingConcept("http://dbpedia.org/Car")
											.equivalentTo("http://schema.org/Car")
										.definingConcept("http://dbpedia.org/Pickup")
											.aSubconceptOf("http://dbpedia.org/Car");
		File types = temporary.namedFile("http://entity##type##http://schema.org/Car"
										+ "\n"
										+ "http://entity##type##http://dbpedia.org/Pickup", "s_types.nt");
		File directory = temporary.directory();
		
		minimalTypesFrom(ontology).computeFor(types, directory);
		
		assertThat(linesOf("s_minType.txt"), hasItem("1##http://entity##http://dbpedia.org/Pickup"));
	}
	
	@Test
	public void equivalentConceptInTheSameHierarchySuperclass() throws Exception {
		ToyOntology ontology = new ToyOntology()
										.owl()
										.definingConcept("http://dbpedia.org/MeanOfTransportation")
										.definingConcept("http://dbpedia.org/Car")
											.equivalentTo("http://schema.org/Car")
											.aSubconceptOf("http://dbpedia.org/MeanOfTransportation");
		File types = temporary.namedFile("http://entity##type##http://schema.org/Car"
										+ "\n"
										+ "http://entity##type##http://dbpedia.org/MeanOfTransportation", "s_types.nt");
		File directory = temporary.directory();
		
		minimalTypesFrom(ontology).computeFor(types, directory);
		
		assertThat(linesOf("s_minType.txt"), hasItem("1##http://entity##http://schema.org/Car"));
	}
	
	private MinimalTypes minimalTypesFrom(ToyOntology ontology) throws Exception {
		
		return new MinimalTypes(getConceptsFrom(ontology), getEquivalentConceptsFrom(ontology), writeSubClassRelationsFrom(ontology));
	}
	
	private List<String> linesOf(String name) throws IOException {
		return FileUtils.readLines(new File(temporary.directory(), name));
	}
	
	private Concepts getConceptsFrom(ToyOntology ontology){
		
		ConceptExtractor conceptExtractor = new ConceptExtractor();
		conceptExtractor.setConcepts(ontology.build());
		
		Concepts concepts = new Concepts();
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
	
	private EquivalentConcepts getEquivalentConceptsFrom(ToyOntology ontology){
		
		EqConceptExtractor equConcepts = new EqConceptExtractor();
		equConcepts.setEquConcept(getConceptsFrom(ontology), ontology.build());
		
		EquivalentConcepts equConcept = new EquivalentConcepts();
		equConcept.setExtractedEquConcept(equConcepts.getExtractedEquConcept());
		equConcept.setEquConcept(equConcepts.getEquConcept());
		
		return equConcept;
	}
}
