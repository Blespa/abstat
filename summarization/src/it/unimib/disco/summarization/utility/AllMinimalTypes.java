package it.unimib.disco.summarization.utility;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import com.hp.hpl.jena.rdf.model.ModelFactory;

public class AllMinimalTypes implements MinimalTypes{

	private HashMap<String, PartitionedMinimalTypes> types;
	private PartitionedMinimalTypes others;
	
	public AllMinimalTypes(File directory) throws Exception {
		this.types = new HashMap<String, PartitionedMinimalTypes>();
		for(File file : new Files().get(directory, "_minType.txt")){
			TextInput input = new TextInput(new FileSystemConnector(file));
			String prefix = new Files().prefixOf(input);
			PartitionedMinimalTypes minimalTypes = new PartitionedMinimalTypes(input);
			if(prefix.equals("others")){
				others = minimalTypes;
			}else{
				types.put(prefix, minimalTypes);
			}
		}
	}

	public List<String> of(String entity) {
		char firstChar = ModelFactory
							.createDefaultModel()
							.createResource(entity)
							.getLocalName()
							.toLowerCase()
							.charAt(0);
		
		PartitionedMinimalTypes minimalTypes = this.types.get(firstChar + "");
		if(minimalTypes != null) return minimalTypes.of(entity);
		return others.of(entity);
	}
}
