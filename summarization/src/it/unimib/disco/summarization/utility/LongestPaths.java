package it.unimib.disco.summarization.utility;

import it.unimib.disco.summarization.datatype.Concept;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

public class LongestPaths {

	private TypeGraph typeGraph;

	public LongestPaths(Concept concepts, String subClassesPath) throws Exception {
		this.typeGraph = new TypeGraph(concepts, subClassesPath);
	}
	
	public void compute(String resultPath) throws Exception {
		
		List<List<String>> allLongestPaths = new ArrayList<List<String>>();
		
		List<String> leaves = typeGraph.leaves();
		List<String> roots = typeGraph.roots();
		
		for(String leaf : leaves){
			for(String root : roots){
				List<List<String>> longestPaths = extractLongestPathsBetween(leaf, root);
				allLongestPaths.addAll(longestPaths);
			}
		}
		
		FileUtils.write(new File(resultPath), StringUtils.join(allLongestPaths, "\n"));
	}

	private List<List<String>> extractLongestPathsBetween(String leaf, String root) {
		int maxLenght = 0;
		List<List<String>> longestPaths = new ArrayList<List<String>>();
		List<List<String>> paths = typeGraph.pathsBetween(leaf, root);
		for(List<String> path : paths){
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
