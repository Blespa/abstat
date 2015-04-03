package it.unimib.disco.summarization.utility;

import it.unimib.disco.summarization.datatype.Concept;

import java.io.File;

import org.apache.commons.lang3.StringUtils;

import com.hp.hpl.jena.ontology.OntResource;

public class MinimalTypes {

	private Concept concepts;
	private TypeGraph graph;

	public MinimalTypes(Concept concepts, File subClassRelations) throws Exception {
		this.concepts = concepts;
		this.graph = new TypeGraph(concepts, new TextInput(new FileSystemConnector(subClassRelations)));
	}

	public void computeFor(File types, File directory) throws Exception {
		String prefix = prefixOf(types);
		BulkTextOutput countConceptFile = connectorTo(directory, prefix, "countConcepts");
		
		for(OntResource concept : concepts.getExtractedConcepts()){
			countConceptFile.writeLine(concept + "##" + 0);
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
