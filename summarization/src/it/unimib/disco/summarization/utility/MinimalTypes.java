package it.unimib.disco.summarization.utility;

import it.unimib.disco.summarization.datatype.Concept;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.semanticweb.yars.nx.parser.NxParser;

import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.vocabulary.OWL;

public class MinimalTypes {

	private Concept concepts;
	private TypeGraph graph;

	public MinimalTypes(Concept concepts, File subClassRelations) throws Exception {
		this.concepts = concepts;
		this.graph = new TypeGraph(concepts, new TextInput(new FileSystemConnector(subClassRelations)));
	}

	public void computeFor(File types, File directory) throws Exception {
		HashMap<String, Integer> conceptCounts = buildConceptCounts();
		List<String> externalConcepts = new ArrayList<String>();
		
		TextInput typeRelations = new TextInput(new FileSystemConnector(types));
		while(typeRelations.hasNextLine()){
			NTriple triple = new NTriple(NxParser.parseNodes(typeRelations.nextLine()));
			
			String entity = triple.subject().uri();
			String concept = triple.object().uri();
			if(!concept.equals(OWL.Thing.getURI())){
				trackConcept(entity, concept, conceptCounts, externalConcepts);
			}
		}
		
		String prefix = prefixOf(types);
		writeConceptCounts(conceptCounts, directory, prefix);
		writeExternalConcepts(externalConcepts, directory, prefix);
		
		connectorTo(directory, prefix, "minType").close();
	}

	private void trackConcept(String entity, String concept, HashMap<String, Integer> counts, List<String> externalConcepts) {
		if(counts.containsKey(concept))	{
			counts.put(concept, counts.get(concept) + 1);
		}else{
			externalConcepts.add(entity + "##" + concept);
		}
	}
	
	private void writeExternalConcepts(List<String> externalConcepts, File directory, String prefix) throws Exception {
		BulkTextOutput externalConceptFile = connectorTo(directory, prefix, "uknHierConcept");
		for(String line : externalConcepts){
			externalConceptFile.writeLine(line);
		}
		externalConceptFile.close();		
	}

	private void writeConceptCounts(HashMap<String, Integer> conceptCounts, File directory, String prefix) throws Exception {
		BulkTextOutput countConceptFile = connectorTo(directory, prefix, "countConcepts");
		for(Entry<String, Integer> concept : conceptCounts.entrySet()){
			countConceptFile.writeLine(concept.getKey() + "##" + concept.getValue());
		}
		countConceptFile.close();
	}

	private HashMap<String, Integer> buildConceptCounts() {
		HashMap<String, Integer> conceptCounts = new HashMap<String, Integer>();
		for(OntResource concept : concepts.getExtractedConcepts()){
			conceptCounts.put(concept.getURI(), 0);
		}
		return conceptCounts;
	}

	private BulkTextOutput connectorTo(File directory, String prefix, String name) {
		return new BulkTextOutput(new FileSystemConnector(new File(directory, prefix + "_" + name + ".txt")), 1000);
	}

	private String prefixOf(File types) {
		return StringUtils.split(types.getName(), "_")[0];
	}
}
