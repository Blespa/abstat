package it.unimib.disco.summarization.utility;

import it.unimib.disco.summarization.datatype.Concept;

import java.io.File;
import java.util.HashMap;
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
		
		TextInput typeRelations = new TextInput(new FileSystemConnector(types));
		while(typeRelations.hasNextLine()){
			String concept = new NTriple(NxParser.parseNodes(typeRelations.nextLine())).object().uri();
			if(!concept.equals(OWL.Thing.getURI())){
				trackConcept(concept, conceptCounts);
			}
		}
		
		String prefix = prefixOf(types);
		writeConceptCounts(conceptCounts, directory, prefix);
		
		connectorTo(directory, prefix, "minType").close();
		connectorTo(directory, prefix, "uknHierConcept").close();
	}

	private void trackConcept(String concept, HashMap<String, Integer> counts) {
		counts.put(concept, counts.get(concept) + 1);
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
