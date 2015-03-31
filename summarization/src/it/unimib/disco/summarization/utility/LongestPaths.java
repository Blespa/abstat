package it.unimib.disco.summarization.utility;

import it.unimib.disco.summarization.datatype.Concept;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class LongestPaths {

	private TypeGraph typeGraph;

	public LongestPaths(Concept concepts, TextInput subClassesPath) throws Exception {
		this.typeGraph = new TypeGraph(concepts, subClassesPath);
	}
	
	public void writeTo(String resultPath) throws Exception {
		
		List<List<String>> allLongestPaths = new ArrayList<List<String>>();
		
		List<String> leaves = typeGraph.leaves();
		List<String> roots = typeGraph.roots();
		
		for(String leaf : leaves){
			for(String root : roots){
				allLongestPaths.addAll(longestPathsBetween(leaf, root));
			}
		}
		
		new TextOutput(new FileSystemConnector(new File(resultPath)))
						.writeLine(StringUtils.join(allLongestPaths, "\n"))
						.close();
	}

	private List<List<String>> longestPathsBetween(String leaf, String root) {
		int maxLenght = 0;
		List<List<String>> longestPaths = new ArrayList<List<String>>();
		
		for(List<String> path : typeGraph.pathsBetween(leaf, root)){
			if(path.size() > maxLenght){
				longestPaths = new ArrayList<List<String>>();
			}
			if(path.size() >= maxLenght){
				Collections.reverse(path);
				longestPaths.add(path);
				maxLenght = path.size();
			}
		}
		return longestPaths;
	}
}
