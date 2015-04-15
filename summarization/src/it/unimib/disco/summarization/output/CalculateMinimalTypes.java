package it.unimib.disco.summarization.output;

import it.unimib.disco.summarization.datatype.Concepts;
import it.unimib.disco.summarization.datatype.EquivalentConcepts;
import it.unimib.disco.summarization.extraction.ConceptExtractor;
import it.unimib.disco.summarization.extraction.EqConceptExtractor;
import it.unimib.disco.summarization.utility.MinimalTypes;
import it.unimib.disco.summarization.utility.Model;

import java.io.File;

import org.apache.commons.io.FileUtils;

import com.hp.hpl.jena.ontology.OntModel;

public class CalculateMinimalTypes {

	public static void main(String[] args) throws Exception {
		
		File ontology = new File(args[0]);
		File subClasses = new File(args[1]);
		File typesDirectory = new File(args[2]);
		File targetDirectory = new File(args[3]);
		
		OntModel ontologyModel = new Model(null, ontology.getAbsolutePath(),"RDF/XML").getOntologyModel();
		
		Concepts concepts = extractConcepts(ontologyModel);
		EquivalentConcepts equivalentConcepts = extractEquivalentConcepts(ontologyModel, concepts);
		
		MinimalTypes minimalTypes = new MinimalTypes(concepts, equivalentConcepts, subClasses);
		
		for(File typeFile : FileUtils.listFiles(typesDirectory, new String[]{"_types.nt"}, false)){
			minimalTypes.computeFor(typeFile, targetDirectory);
		}
	}

	private static EquivalentConcepts extractEquivalentConcepts(OntModel ontologyModel, Concepts concepts) {
		EqConceptExtractor equConcepts = new EqConceptExtractor();
		equConcepts.setEquConcept(concepts, ontologyModel);
		
		EquivalentConcepts equivalentConcepts = new EquivalentConcepts();
		equivalentConcepts.setExtractedEquConcept(equConcepts.getExtractedEquConcept());
		equivalentConcepts.setEquConcept(equConcepts.getEquConcept());
		return equivalentConcepts;
	}

	private static Concepts extractConcepts(OntModel ontologyModel) {
		ConceptExtractor cExtract = new ConceptExtractor();
		cExtract.setConcepts(ontologyModel);
		
		Concepts concepts = new Concepts();
		concepts.setConcepts(cExtract.getConcepts());
		concepts.setExtractedConcepts(cExtract.getExtractedConcepts());
		concepts.setObtainedBy(cExtract.getObtainedBy());
		return concepts;
	}
}
