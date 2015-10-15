package it.unimib.disco.summarization.test.unit;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import it.unimib.disco.summarization.dataset.MinimalTypesCalculation;
import it.unimib.disco.summarization.dataset.TextInput;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDFS;

public class MinimalTypesCalculationTest extends TestWithTemporaryData{

	@Test
	public void shouldParseFileNamesWithStrangeSeparators() throws Exception {
		
		ToyOntology ontology = new ToyOntology().owl();
		
		TextInput types = temporary.namedFileTextInput("", "__types.nt");
		File directory = temporary.directory();
		
		minimalTypesFrom(ontology, directory).process(types);
		
		assertThat(new File(directory, "__minType.txt").exists(), is(true));
	}
	
	@Test
	public void shouldParseFileNamesThatAreMoreThanOneCharLong() throws Exception {
		
		ToyOntology ontology = new ToyOntology().owl();
		
		TextInput types = temporary.namedFileTextInput("", "others_types.nt");
		File directory = temporary.directory();
		
		minimalTypesFrom(ontology, directory).process(types);
		
		assertThat(new File(directory, "others_minType.txt").exists(), is(true));
	}
	
	@Test
	public void shouldParseAnEmptyTripleFile() throws Exception {
		
		ToyOntology ontology = new ToyOntology().owl();
		
		TextInput types = temporary.namedFileTextInput("", "s_types.nt");
		File directory = temporary.directory();
		
		minimalTypesFrom(ontology, directory).process(types);
		
		assertThat(new File(directory, "s_minType.txt").exists(), is(true));
		assertThat(new File(directory, "s_uknHierConcept.txt").exists(), is(true));
		assertThat(new File(directory, "s_countConcepts.txt").exists(), is(true));
	}

	@Test
	public void shouldSkipOwlThing() throws Exception {
		
		ToyOntology ontology = new ToyOntology().owl();
		
		TextInput types = temporary.namedFileTextInput("", "s_types.nt");
		File directory = temporary.directory();
		
		minimalTypesFrom(ontology, directory).process(types);
		
		assertThat(new File(directory, "s_minType.txt").exists(), is(true));
		assertThat(new File(directory, "s_uknHierConcept.txt").exists(), is(true));
		assertThat(new File(directory, "s_countConcepts.txt").exists(), is(true));
	}
	
	@Test
	public void shouldCountConceptsEvenWhenTheyHaveNoInstance() throws Exception {
		ToyOntology ontology = new ToyOntology().owl();
		
		TextInput types = temporary.namedFileTextInput("http://entity##type##" + OWL.Thing, "s_types.nt");
		File directory = temporary.directory();
		
		minimalTypesFrom(ontology, directory).process(types);
		
		assertThat(linesOf("s_minType.txt"), hasSize(0));
		assertThat(linesOf("s_uknHierConcept.txt"), hasSize(0));
		assertThat(linesOf("s_countConcepts.txt"), hasSize(0));
	}
	
	@Test
	public void shouldCountTheRightNumberOfConcepts() throws Exception {
		ToyOntology ontology = new ToyOntology()
								.owl()
								.definingConcept("http://concept");

		TextInput types = temporary.namedFileTextInput("http://entity##type##http://concept", "s_types.nt");
		File directory = temporary.directory();
		
		minimalTypesFrom(ontology, directory).process(types);
		List<String> conceptCounts = linesOf("s_countConcepts.txt");
		
		assertThat(conceptCounts, hasItem("http://concept##1"));
	}
	
	@Test
	public void shouldCountAlsoUnknownConcepts() throws Exception {
		ToyOntology ontology = new ToyOntology().owl();

		TextInput types = temporary.namedFileTextInput("http://entity##type##http://concept", "s_types.nt");
		File directory = temporary.directory();
		
		minimalTypesFrom(ontology, directory).process(types);
		
		assertThat(linesOf("s_countConcepts.txt"), is(empty()));
		assertThat(linesOf("s_uknHierConcept.txt"), hasItem("http://entity##http://concept"));
	}
	
	@Test
	public void shouldTrackASimpleMinimalType() throws Exception {
		ToyOntology ontology = new ToyOntology()
										.owl()
										.definingConcept("http://concept");

		TextInput types = temporary.namedFileTextInput("http://entity##type##http://concept", "s_types.nt");
		File directory = temporary.directory();
		
		minimalTypesFrom(ontology, directory).process(types);
		
		assertThat(linesOf("s_minType.txt"), hasItem("1##http://entity##http://concept"));
	}
	
	@Test
	public void shouldExcludeNonMinimalTypes() throws Exception {
		ToyOntology ontology = new ToyOntology()
										.owl()
										.definingConcept("http://thing")
										.definingConcept("http://concept")
											.aSubconceptOf("http://thing");

		TextInput types = temporary.namedFileTextInput("http://entity##type##http://concept"
										+ "\n"
										+ "http://entity##type##http://thing", "s_types.nt");
		File directory = temporary.directory();
		
		minimalTypesFrom(ontology, directory).process(types);
		
		assertThat(linesOf("s_minType.txt"), hasItem("1##http://entity##http://concept"));
	}
	
	@Test
	public void shouldReplaceANonMinimalTypeWhenAMinimalIsFound() throws Exception {
		ToyOntology ontology = new ToyOntology()
										.owl()
										.definingConcept("http://thing")
										.definingConcept("http://concept")
											.aSubconceptOf("http://thing");

		TextInput types = temporary.namedFileTextInput("http://entity##type##http://thing"
										+ "\n"
										+ "http://entity##type##http://concept", "s_types.nt");
		File directory = temporary.directory();
		
		minimalTypesFrom(ontology, directory).process(types);
		
		assertThat(linesOf("s_minType.txt"), hasItem("1##http://entity##http://concept"));
	}
	
	@Test
	public void manyMinimalTypes() throws Exception {
		ToyOntology ontology = new ToyOntology()
									.owl()
									.definingConcept("http://thing")
									.definingConcept("http://concept");

		TextInput types = temporary.namedFileTextInput("http://entity##type##http://thing"
										+ "\n"
										+ "http://entity##type##http://concept", "s_types.nt");
		File directory = temporary.directory();
		
		minimalTypesFrom(ontology, directory).process(types);
		
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

		TextInput types = temporary.namedFileTextInput("http://entity##type##http://other"
										+ "\n"
										+ "http://entity##type##http://zthing"
										+ "\n"
										+ "http://entity##type##http://concept", "s_types.nt");
		File directory = temporary.directory();

		minimalTypesFrom(ontology, directory).process(types);

		assertThat(linesOf("s_minType.txt"), hasItem("2##http://entity##http://concept#-#http://other"));
	}
	
	@Test
	public void equivalentConcept() throws Exception {
		ToyOntology ontology = new ToyOntology()
										.owl()
										.definingConcept("http://dbpedia.org/Person")
										.equivalentTo("http://schema.org/Person");

		TextInput types = temporary.namedFileTextInput("http://entity##type##http://dbpedia.org/Person"
					+ "\n"
					+ "http://entity##type##http://schema.org/Person", "s_types.nt");
		File directory = temporary.directory();
		
		minimalTypesFrom(ontology, directory).process(types);
		
		assertThat(linesOf("s_minType.txt"), hasItem("2##http://entity##http://dbpedia.org/Person#-#http://schema.org/Person"));
	}
	
	@Test
	public void unknownConceptForAnEntity() throws Exception {
		ToyOntology ontology = new ToyOntology().owl().definingConcept("http://dbpedia.org/Person");

		TextInput types = temporary.namedFileTextInput("http://entity##type##http://dbpedia.org/Person"
										+ "\n"
										+ "http://entity##type##http://unknown", "s_types.nt");
		File directory = temporary.directory();
		
		minimalTypesFrom(ontology, directory).process(types);

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
		
		TextInput types = temporary.namedFileTextInput("http://steven_seagal##type##http://person", "s_types.nt");
		File directory = temporary.directory();
		
		minimalTypesFrom(ontology, directory).process(types);
		
		assertThat(linesOf("s_countConcepts.txt"), hasItem("http://datatype/age##0"));
		assertThat(linesOf("s_countConcepts.txt"), hasItem("http://person##1"));
	}
	
	private MinimalTypesCalculation minimalTypesFrom(ToyOntology ontology, File directory) throws Exception {
		
		return new MinimalTypesCalculation(ontology.build(), directory);
	}
	
	private List<String> linesOf(String name) throws IOException {
		return FileUtils.readLines(new File(temporary.directory(), name));
	}
}
