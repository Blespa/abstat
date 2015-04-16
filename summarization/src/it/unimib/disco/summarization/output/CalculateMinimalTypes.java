package it.unimib.disco.summarization.output;

import it.unimib.disco.summarization.datatype.Concepts;
import it.unimib.disco.summarization.datatype.EquivalentConcepts;
import it.unimib.disco.summarization.extraction.ConceptExtractor;
import it.unimib.disco.summarization.extraction.EqConceptExtractor;
import it.unimib.disco.summarization.starter.Events;
import it.unimib.disco.summarization.utility.MinimalTypes;
import it.unimib.disco.summarization.utility.Model;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import com.hp.hpl.jena.ontology.OntModel;

public class CalculateMinimalTypes {

	public static void main(String[] args) throws Exception {
		
		final Events logger = new Events();
		logger.info(StringUtils.join(args, " "));
		
		File folder = new File(args[0]);
		Collection<File> listOfFiles = FileUtils.listFiles(folder, new String[]{"owl"}, false);
		File ontology = listOfFiles.iterator().next();
		File subClasses = new File(args[1]);
		final File conceptFile = new File(args[2]);
		File typesDirectory = new File(args[3]);
		final File targetDirectory = new File(args[4]);
		
		OntModel ontologyModel = new Model(null, ontology.getAbsolutePath(),"RDF/XML").getOntologyModel();
		
		Concepts concepts = extractConcepts(ontologyModel);
		EquivalentConcepts equivalentConcepts = extractEquivalentConcepts(ontologyModel, concepts);
		
		final MinimalTypes minimalTypes = new MinimalTypes(concepts, equivalentConcepts, subClasses);
		
		File[] files = typesDirectory.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith("_types.nt");
			}
		});
				
		logger.info(StringUtils.join(files, " "));
		
		ExecutorService executor = Executors.newFixedThreadPool(10);
		for(final File typeFile : files){
			executor.execute(new Runnable() {
				@Override
				public void run() {
					try {
						logger.info("computing minimal types for " + typeFile);
						minimalTypes.computeFor(conceptFile, typeFile, targetDirectory);
						logger.info("done: " + typeFile);
					} catch (Exception e) {
						logger.error(typeFile, e);
					}
				}
			});
		}
		executor.shutdown();
	    while(!executor.isTerminated()){}
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
