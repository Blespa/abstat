package it.unimib.disco.summarization.utility;

import it.unimib.disco.summarization.datatype.Concept;

import java.io.File;

import org.apache.commons.io.FileUtils;

public class LongestPath {

	public LongestPath(Concept concepts, String subClassesPath) {
	}

	public void compute(String resultPath) throws Exception {
		
		FileUtils.write(new File(resultPath), "");
	}
}
