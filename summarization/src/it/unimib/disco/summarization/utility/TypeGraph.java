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

public class TypeGraph{
	
	private DirectedAcyclicGraph<String, DefaultEdge> graph;

	public TypeGraph(Concept concepts, String subClassesPath) throws Exception{
		this.graph = typeGraphFrom(concepts, FileUtils.readLines(new File(subClassesPath)));
	}
	
	public List<String> roots(){
		ArrayList<String> roots = new ArrayList<String>();
		for(String concept : graph.vertexSet()){
			if(isRoot(concept)) roots.add(concept);
		}
		return roots;
	}
	
	public List<String> leaves(){
		ArrayList<String> leaves = new ArrayList<String>();
		for(String concept : graph.vertexSet()){
			if(isLeaf(concept)) leaves.add(concept);
		}
		return leaves;
	}
	
	public List<List<String>> pathsBetween(String leaf, String root){
		ArrayList<List<String>> paths = new ArrayList<List<String>>();
		enumeratePathsBetween(leaf, root, new ArrayList<String>(), paths);
		return paths;
	}
	
	private void enumeratePathsBetween(String leaf, String root, List<String> currentPath, List<List<String>> paths){
		ArrayList<String> path = new ArrayList<String>(currentPath);
		path.add(leaf);
		if(leaf.equals(root)){
			paths.add(path);
		}
		for(DefaultEdge edgeToSuperType : graph.outgoingEdgesOf(leaf)){
			String superType = graph.getEdgeTarget(edgeToSuperType);
			enumeratePathsBetween(superType, root, path, paths);
		}
	}
	
	private boolean isLeaf(String concept){
		return graph.incomingEdgesOf(concept).isEmpty();
	}
	
	private boolean isRoot(String concept){
		return graph.outgoingEdgesOf(concept).isEmpty();
	}
	
	private DirectedAcyclicGraph<String, DefaultEdge> typeGraphFrom(Concept concepts, List<String> subclassRelations) throws Exception {
		DirectedAcyclicGraph<String, DefaultEdge> typeGraph = new DirectedAcyclicGraph<String, DefaultEdge>(DefaultEdge.class);
		
		for(OntResource concept : concepts.getExtractedConcepts()){
			typeGraph.addVertex(concept.getURI());
		}
		
		for(String line : subclassRelations){
			String[] relation = line.split("##");
			String subtype = relation[0];
			String supertype = relation[1];
			
			if(!typeGraph.containsVertex(subtype)) typeGraph.addVertex(subtype);
			if(!typeGraph.containsVertex(supertype)) typeGraph.addVertex(supertype);
			
			typeGraph.addDagEdge(subtype, supertype);
		}
		
		return typeGraph;
	}
}