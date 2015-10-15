package it.unimib.disco.summarization.export;

import it.unimib.disco.summarization.ontology.LDSummariesVocabulary;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;

public class WriteConceptGraphToRDF {

	public static void main(String[] args) throws Exception {
		String input = args[0];
		String output = args[1];
		String dataset = args[2];
		
		Model model = ModelFactory.createDefaultModel();
		LDSummariesVocabulary vocabulary = new LDSummariesVocabulary(model, dataset);
		
		List<String> lines = FileUtils.readLines(new File(input));
		for(String line : lines){
			String[] splitted = StringUtils.split(line, "##");
			
			Resource localConcept = vocabulary.addConcept(splitted[0]);
			Resource localBroaderConcept = vocabulary.addConcept(splitted[1]);
			
			model.add(localConcept, vocabulary.broader(), localBroaderConcept);
		}
		
		OutputStream file = new FileOutputStream(output);
		model.write(file, "N-Triples", null);
		file.close();
	}
}
