package it.unimib.disco.summarization.utility;

import it.unimib.disco.summarization.datatype.Concepts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;

import org.jgraph.graph.DefaultEdge;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DirectedPseudograph;

import com.hp.hpl.jena.ontology.OntResource;

public class TypeGraph{
	
	private DirectedGraph<String, DefaultEdge> graph;

	public TypeGraph(Concepts concepts, TextInput subClassesPath) throws Exception{
		this.graph = subTypeGraphFrom(concepts, subClassesPath);
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
		inOrderTraversal(leaf, root, new ArrayList<String>(), paths);
		return paths;
	}
	
	public void enrichWith(HashMap<String, HashSet<String>> equivalentConcepts) throws Exception {
		for(Entry<String, HashSet<String>> equivalences : equivalentConcepts.entrySet()){
			for(String equivalentConcept : equivalences.getValue()){
				graph.addVertex(equivalentConcept);
				for(DefaultEdge edgeToSuperType : graph.outgoingEdgesOf(equivalences.getKey())){
					String supertype = graph.getEdgeTarget(edgeToSuperType);
					graph.addEdge(equivalentConcept, supertype);
				}
				for(DefaultEdge edgeToSubType : graph.incomingEdgesOf(equivalences.getKey())){
					String subtype = graph.getEdgeSource(edgeToSubType);
					graph.addEdge(subtype, equivalentConcept);
				}
			}
		}
	}
	
	private void inOrderTraversal(String leaf, String root, List<String> currentPath, List<List<String>> paths){
		ArrayList<String> path = new ArrayList<String>(currentPath);
		path.add(leaf);
		if(leaf.equals(root)){
			paths.add(path);
		}
		for(DefaultEdge edgeToSuperType : graph.outgoingEdgesOf(leaf)){
			String superType = graph.getEdgeTarget(edgeToSuperType);
			inOrderTraversal(superType, root, path, paths);
		}
	}
	
	private boolean isLeaf(String concept){
		return graph.incomingEdgesOf(concept).isEmpty();
	}
	
	private boolean isRoot(String concept){
		return graph.outgoingEdgesOf(concept).isEmpty();
	}
	
	private DirectedGraph<String, DefaultEdge> subTypeGraphFrom(Concepts concepts, TextInput subclassRelations) throws Exception {
		DirectedGraph<String, DefaultEdge> typeGraph = new DirectedPseudograph<String, DefaultEdge>(DefaultEdge.class);
		
		for(OntResource concept : concepts.getExtractedConcepts()){
			typeGraph.addVertex(concept.getURI());
		}
		
		while(subclassRelations.hasNextLine()){
			String line = subclassRelations.nextLine();
			String[] relation = line.split("##");
			String subtype = relation[0];
			String supertype = relation[1];
			
			if(!typeGraph.containsVertex(subtype)) typeGraph.addVertex(subtype);
			if(!typeGraph.containsVertex(supertype)) typeGraph.addVertex(supertype);
			
			typeGraph.addEdge(subtype, supertype);
		}
		
		return typeGraph;
	}
}