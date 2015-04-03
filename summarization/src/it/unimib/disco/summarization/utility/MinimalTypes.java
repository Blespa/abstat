package it.unimib.disco.summarization.utility;

import it.unimib.disco.summarization.datatype.Concept;

import java.io.File;
import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;
import org.semanticweb.yars.nx.parser.NxParser;

import com.hp.hpl.jena.ontology.OntResource;

public class MinimalTypes {

	private Concept concepts;
	private TypeGraph graph;

	public MinimalTypes(Concept concepts, File subClassRelations) throws Exception {
		this.concepts = concepts;
		this.graph = new TypeGraph(concepts, new TextInput(new FileSystemConnector(subClassRelations)));
	}

	public void computeFor(File types, File directory) throws Exception {
		HashMap<String, Integer> conceptCounts = new HashMap<String, Integer>();
		for(OntResource concept : concepts.getExtractedConcepts()){
			conceptCounts.put(concept.getURI(), 0);
		}
		TextInput typeRelations = new TextInput(new FileSystemConnector(types));
		while(typeRelations.hasNextLine()){
			NTriple triple = new NTriple(NxParser.parseNodes(typeRelations.nextLine()));
			conceptCounts.put(triple.object().uri(), conceptCounts.get(triple.object().uri()) + 1);
		}
		
		String prefix = prefixOf(types);
		BulkTextOutput countConceptFile = connectorTo(directory, prefix, "countConcepts");
		for(String concept : conceptCounts.keySet()){
			countConceptFile.writeLine(concept + "##" + conceptCounts.get(concept));
		}
		
		connectorTo(directory, prefix, "minType").close();
		connectorTo(directory, prefix, "newConcepts").close();
		connectorTo(directory, prefix, "uknHierConcept").close();
		countConceptFile.close();
	}

	private BulkTextOutput connectorTo(File directory, String prefix, String name) {
		return new BulkTextOutput(new FileSystemConnector(new File(directory, prefix + "_" + name + ".txt")), 1000);
	}

	private String prefixOf(File types) {
		return StringUtils.split(types.getName(), "_")[0];
	}
}
