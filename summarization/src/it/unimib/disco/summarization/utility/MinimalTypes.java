package it.unimib.disco.summarization.utility;

import it.unimib.disco.summarization.datatype.Concept;

import java.io.File;

import org.apache.commons.lang3.StringUtils;

public class MinimalTypes {

	public MinimalTypes(Concept conceptsFrom, File subClassRelations) {
	}

	public void computeFor(File types, File directory) throws Exception {
		String prefix = prefixOf(types);
		
		connectorTo(directory, prefix, "minType").close();
		connectorTo(directory, prefix, "newConcepts").close();
		connectorTo(directory, prefix, "uknHierConcept").close();
		connectorTo(directory, prefix, "countConcepts").close();
	}

	private BulkTextOutput connectorTo(File directory, String prefix, String name) {
		return new BulkTextOutput(new FileSystemConnector(new File(directory, prefix + "_" + name + ".txt")), 1000);
	}

	private String prefixOf(File types) {
		return StringUtils.split(types.getName(), "_")[0];
	}
}
