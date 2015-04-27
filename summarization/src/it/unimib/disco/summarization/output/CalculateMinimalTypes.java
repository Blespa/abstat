package it.unimib.disco.summarization.output;

import it.unimib.disco.summarization.starter.Events;
import it.unimib.disco.summarization.utility.MinimalTypes;
import it.unimib.disco.summarization.utility.Model;
import it.unimib.disco.summarization.utility.ParallelProcessing;

import java.io.File;
import java.util.Collection;

import org.apache.commons.io.FileUtils;

import com.hp.hpl.jena.ontology.OntModel;

public class CalculateMinimalTypes {

	public static void main(String[] args) throws Exception {
		
		new Events();
		
		File folder = new File(args[0]);
		Collection<File> listOfFiles = FileUtils.listFiles(folder, new String[]{"owl"}, false);
		File ontology = listOfFiles.iterator().next();
		File typesDirectory = new File(args[1]);
		File targetDirectory = new File(args[2]);
		
		OntModel ontologyModel = new Model(null, ontology.getAbsolutePath(),"RDF/XML").getOntologyModel();
		
		MinimalTypes minimalTypes = new MinimalTypes(ontologyModel, targetDirectory);
		
		new ParallelProcessing(typesDirectory, "_types.nt").process(minimalTypes);
	}
}
