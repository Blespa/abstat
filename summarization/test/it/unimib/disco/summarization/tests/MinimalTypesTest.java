package it.unimib.disco.summarization.tests;

import it.unimib.disco.summarization.datatype.Concept;
import it.unimib.disco.summarization.extraction.ConceptExtractor;
import it.unimib.disco.summarization.relation.OntologySubclassOfExtractor;
import it.unimib.disco.summarization.utility.MinimalTypes;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import com.hp.hpl.jena.ontology.OntClass;

public class MinimalTypesTest extends TestWithTemporaryData{

	@Test
	public void shouldParseAnEmptyTripleFile() throws Exception {
		
		ToyOntology ontology = new ToyOntology().owl();
		
		File types = temporary.file();
		File directory = temporary.directory();
		
		new MinimalTypes(getConceptsFrom(ontology), writeSubClassRelationsFrom(ontology)).computeFor(types, directory);
		
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
