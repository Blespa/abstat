package it.unimib.disco.summarization.utility;

import it.unimib.disco.summarization.datatype.Concept;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.jgraph.graph.DefaultEdge;
import org.jgrapht.experimental.dag.DirectedAcyclicGraph;

import com.hp.hpl.jena.ontology.OntResource;

public class LongestPath {

	private TypeGraph typeGraph;

	public LongestPath(Concept concepts, String subClassesPath) throws Exception {
		this.typeGraph = new TypeGraph(concepts, subClassesPath);
	}
	
	public void compute(String resultPath) throws Exception {
		
		List<String> results = new ArrayList<String>();
		for(String type : typeGraph.isolatedTypes()){
			results.add("[" + type + "]");
		}
		
		FileUtils.writeLines(new File(resultPath), results);
	}
}

class TypeGraph{
	
	private DirectedAcyclicGraph<String, DefaultEdge> graph;

	public TypeGraph(Concept concepts, String subClassesPath) throws Exception{
		this.graph = typeGraphFrom(concepts, FileUtils.readLines(new File(subClassesPath)));
	}
	
	public List<String> isolatedTypes(){
		ArrayList<String> isolatedConcepts = new ArrayList<String>();
		for(String concept : graph.vertexSet()){
			if(graph.edgesOf(concept).isEmpty()) isolatedConcepts.add(concept);
		}
		return isolatedConcepts;
	}
	
	private DirectedAcyclicGraph<String, DefaultEdge> typeGraphFrom(Concept concepts, List<String> subclassRelations) throws Exception {
		
		DirectedAcyclicGraph<String, DefaultEdge> typeGraph = new DirectedAcyclicGraph<String, DefaultEdge>(DefaultEdge.class);
		for(OntResource concept : concepts.getExtractedConcepts()){
			typeGraph.addVertex(concept.getURI());
		}
		for(String line : subclassRelations){
			String[] relation = StringUtils.split(line, "##");
			typeGraph.addDagEdge(relation[0], relation[1]);
		}
		return typeGraph;
	}
}
