package it.unimib.disco.summarization.utility;

import it.unimib.disco.summarization.datatype.Concept;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.hp.hpl.jena.ontology.OntResource;

public class LongestPath {

	private Concept concepts;

	public LongestPath(Concept concepts, String subClassesPath) {
		this.concepts = concepts;
	}

	public void compute(String resultPath) throws Exception {
		List<String> results = new ArrayList<String>();
		
		for(OntResource concept : concepts.getExtractedConcepts()){
			results.add("[" + concept + "]");
		}
		
		FileUtils.writeLines(new File(resultPath), results);
	}
}
