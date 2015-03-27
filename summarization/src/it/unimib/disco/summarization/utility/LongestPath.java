package it.unimib.disco.summarization.utility;

import it.unimib.disco.summarization.datatype.Concept;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

public class LongestPath {

	private TypeGraph typeGraph;

	public LongestPath(Concept concepts, String subClassesPath) throws Exception {
		this.typeGraph = new TypeGraph(concepts, subClassesPath);
	}
	
	public void compute(String resultPath) throws Exception {
		
		List<List<String>> allLongestPaths = new ArrayList<List<String>>();
		
		for(String leaf : typeGraph.leaves()){
			for(String root : typeGraph.roots()){
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
				allLongestPaths.addAll(longestPaths);
			}
		}
		
		FileUtils.write(new File(resultPath), StringUtils.join(allLongestPaths, "\n"));
	}
}
