package it.unimib.disco.summarization.output;

import it.unimib.disco.summarization.starter.Events;
import it.unimib.disco.summarization.utility.MinimalTypes;
import it.unimib.disco.summarization.utility.Model;

import java.io.File;
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
		File typesDirectory = new File(args[1]);
		final File targetDirectory = new File(args[2]);
		
		OntModel ontologyModel = new Model(null, ontology.getAbsolutePath(),"RDF/XML").getOntologyModel();
		
		final MinimalTypes minimalTypes = new MinimalTypes(ontologyModel);
		
		ExecutorService executor = Executors.newFixedThreadPool(10);
		for(final File typeFile : new Files().get(typesDirectory, "_types.nt")){
			executor.execute(new Runnable() {
				@Override
				public void run() {
					try {
						logger.info("computing minimal types for " + typeFile);
						minimalTypes.computeFor(typeFile, targetDirectory);
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

	
}
