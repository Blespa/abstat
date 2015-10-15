package it.unimib.disco.summarization.export;

import it.unimib.disco.summarization.dataset.MinimalTypesCalculation;
import it.unimib.disco.summarization.dataset.ParallelProcessing;
import it.unimib.disco.summarization.ontology.Model;

import java.io.File;
import java.util.Collection;

import org.apache.commons.io.FileUtils;

import com.hp.hpl.jena.ontology.OntModel;

public class CalculateMinimalTypes {

	public static void main(String[] args) throws Exception {
		
		Events.summarization();
		
		File folder = new File(args[0]);
		Collection<File> listOfFiles = FileUtils.listFiles(folder, new String[]{"owl"}, false);
		File ontology = listOfFiles.iterator().next();
		File typesDirectory = new File(args[1]);
		File targetDirectory = new File(args[2]);
		
		OntModel ontologyModel = new Model(ontology.getAbsolutePath(),"RDF/XML").getOntologyModel();
		
		MinimalTypesCalculation minimalTypes = new MinimalTypesCalculation(ontologyModel, targetDirectory);
		
		new ParallelProcessing(typesDirectory, "_types.nt").process(minimalTypes);
	}
}
