package it.unimib.disco.summarization.tests;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.*;
import it.unimib.disco.summarization.datatype.Concept;
import it.unimib.disco.summarization.extraction.ConceptExtractor;
import it.unimib.disco.summarization.relation.OntologySubclassOfExtractor;
import it.unimib.disco.summarization.utility.ComputeLongestPathHierarchy;
import it.unimib.disco.summarization.utility.LongestPaths;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import com.hp.hpl.jena.ontology.OntClass;

public class LongestPathsTest extends TestWithTemporaryData{

	@Test
	public void shouldAddAlsoExternalTypes() throws Exception {
		
		Concept concepts = getConceptsFrom(new ToyOntology()
													.owl()
													.definingConcept("http://concept"));
		File subClasses = writeSubClassRelationsFrom(new ToyOntology()
											.owl()
											.definingConcept("http://concept")
												.aSubconceptOf("http://external-ontology#Concept"));
		
		List<String> paths = linesFrom(longestPaths(concepts, subClasses));
		
		assertThat(paths, hasItem("[http://external-ontology#Concept, http://concept]"));
	}
	
	@Test
	public void shouldHandleAnEmptyOntology() throws Exception {
		
		ToyOntology ontology = new ToyOntology().owl();
		Concept concepts = getConceptsFrom(ontology);
		File subClasses = writeSubClassRelationsFrom(ontology);
		
		File savedPaths = longestPaths(concepts, subClasses);
		
		assertThat(linesFrom(savedPaths), empty());
	}
	
	@Test
	public void shouldPrintIsolatedConcepts() throws Exception {
		
		ToyOntology ontology = new ToyOntology()
										.owl()
										.definingConcept("http://concept");
		
		Concept concepts = getConceptsFrom(ontology);
		File subClasses = writeSubClassRelationsFrom(ontology);
		
		List<String> paths = linesFrom(longestPaths(concepts, subClasses));
		
		assertThat(paths, hasSize(1));
		assertThat(paths, hasItem("[http://concept]"));
	}
	
	@Test
	public void shouldNotPrintConnectedConcepts() throws Exception {
		
		ToyOntology ontology = new ToyOntology()
										.owl()
										.definingConcept("http://isolated-concept")
										.definingConcept("http://agent")
											.aSubconceptOf("http://thing");
		
		Concept concepts = getConceptsFrom(ontology);
		File subClasses = writeSubClassRelationsFrom(ontology);
		
		List<String> paths = linesFrom(longestPaths(concepts, subClasses));
		
		assertThat(paths, hasItem("[http://isolated-concept]"));
		assertThat(paths, not(hasItem("[http://http://agent]")));
		assertThat(paths, not(hasItem("[http://thing]")));
	}
	
	@Test
	public void shouldPrintAPath() throws Exception {
		
		ToyOntology ontology = new ToyOntology()
										.owl()
										.definingConcept("http://agent")
											.aSubconceptOf("http://thing");
		
		Concept concepts = getConceptsFrom(ontology);
		File subClasses = writeSubClassRelationsFrom(ontology);
		
		List<String> paths = linesFrom(longestPaths(concepts, subClasses));
		
		assertThat(paths, hasItem("[http://thing, http://agent]"));
	}

	@Test
	public void shouldComputeTheSamePathsThanLegacyCode() throws Exception {
		
		String root = "http://root";
		String person = "http://person";
		String agent = "http://agent";
		String player = "http://player";
		String place = "http://place";
		String building = "http://building";
		
		ToyOntology ontology = new ToyOntology()
				.owl()
				.definingConcept(root)
				.definingConcept(person)
					.aSubconceptOf(agent)
				.definingConcept(player)
					.aSubconceptOf(person)
				.definingConcept(place)
					.aSubconceptOf(root)
				.definingConcept(building)
					.aSubconceptOf(root)
					.aSubconceptOf(place);
		
		Concept concepts = getConceptsFrom(ontology);
		File subClasses = writeSubClassRelationsFrom(ontology);
		
		File legacyResults = temporary.newFile();
		new ComputeLongestPathHierarchy(concepts, subClasses.getAbsolutePath()).computeLonghestPathHierarchy(legacyResults.getAbsolutePath());
		
		assertAreEquivalent(legacyResults, longestPaths(concepts, subClasses));
	}

	private File longestPaths(Concept concepts, File subClasses) throws Exception {
		File results = temporary.newFile();
		new LongestPaths(concepts, subClasses.getAbsolutePath()).writeTo(results.getAbsolutePath());
		return results;
	}
	
	private void assertAreEquivalent(File legacyResults, File results) throws Exception {
		Collection<String> legacyPaths = linesFrom(legacyResults);
		Collection<String> paths = linesFrom(results);
		
		assertThat(paths, hasSize(legacyPaths.size()));
		assertThat(paths, containsInAnyOrder(legacyPaths.toArray()));
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
		
		return temporary.newFile(StringUtils.join(result, "\n"));
	}
	
	private List<String> linesFrom(File savedPaths) throws IOException {
		return FileUtils.readLines(savedPaths);
	}
}
