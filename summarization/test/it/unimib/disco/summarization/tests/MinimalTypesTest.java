package it.unimib.disco.summarization.tests;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import it.unimib.disco.summarization.utility.MinimalTypes;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDFS;

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
		
		assertThat(linesOf("s_minType.txt"), hasItem("2##http://entity##http://concept#-#http://thing"));
	}
	
	@Test
	public void manyMinimalTypesWithOneExclusionAndStrangeOrdering() throws Exception {
		ToyOntology ontology = new ToyOntology()
										.owl()
										.definingConcept("http://zthing")
										.definingConcept("http://concept")
											.aSubconceptOf("http://zthing")
										.definingConcept("http://other");

		File types = temporary.namedFile("http://entity##type##http://other"
										+ "\n"
										+ "http://entity##type##http://zthing"
										+ "\n"
										+ "http://entity##type##http://concept", "s_types.nt");
		File directory = temporary.directory();

		minimalTypesFrom(ontology).computeFor(types, directory);

		assertThat(linesOf("s_minType.txt"), hasItem("2##http://entity##http://other#-#http://concept"));
	}
	
	@Test
	public void equivalentConcept() throws Exception {
		ToyOntology ontology = new ToyOntology()
										.owl()
										.definingConcept("http://dbpedia.org/Person")
										.equivalentTo("http://schema.org/Person");

		File types = temporary.namedFile("http://entity##type##http://dbpedia.org/Person"
					+ "\n"
					+ "http://entity##type##http://schema.org/Person", "s_types.nt");
		File directory = temporary.directory();
		
		minimalTypesFrom(ontology).computeFor(types, directory);
		
		assertThat(linesOf("s_minType.txt"), hasItem("2##http://entity##http://schema.org/Person#-#http://dbpedia.org/Person"));
	}
	
	@Test
	public void unknownConceptForAnEntity() throws Exception {
		ToyOntology ontology = new ToyOntology().owl().definingConcept("http://dbpedia.org/Person");

		File types = temporary.namedFile("http://entity##type##http://dbpedia.org/Person"
										+ "\n"
										+ "http://entity##type##http://unknown", "s_types.nt");
		File directory = temporary.directory();
		
		minimalTypesFrom(ontology).computeFor(types, directory);

		assertThat(linesOf("s_uknHierConcept.txt"), hasItem("http://entity##http://unknown"));
	}
	
	@Test
	public void shouldGetAlsoConceptsThatAreMentionedAsDomaninOrRange() throws Exception {
		ToyOntology ontology = new ToyOntology()
										.owl()
										.definingResource("http://age")
											.thatHasProperty(RDFS.domain)
												.linkingTo("http://person")
											.thatHasProperty(RDFS.range)
												.linkingTo("http://datatype/age");
		
		File types = temporary.namedFile("http://steven_seagal##type##http://person", "s_types.nt");
		File directory = temporary.directory();
		
		minimalTypesFrom(ontology).computeFor(types, directory);
		
		assertThat(linesOf("s_countConcepts.txt"), hasItem("http://datatype/age##0"));
		assertThat(linesOf("s_countConcepts.txt"), hasItem("http://person##1"));
	}
	
	private MinimalTypes minimalTypesFrom(ToyOntology ontology) throws Exception {
		
		return new MinimalTypes(ontology.build());
	}
	
	private List<String> linesOf(String name) throws IOException {
		return FileUtils.readLines(new File(temporary.directory(), name));
	}
}
