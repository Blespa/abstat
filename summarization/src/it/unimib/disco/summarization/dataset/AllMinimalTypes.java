package it.unimib.disco.summarization.dataset;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

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
		String[] splitted = StringUtils.split(entity, "/");
		String firstChar = splitted[splitted.length - 1].toLowerCase().charAt(0) + "";
		PartitionedMinimalTypes minimalTypes = this.types.get(firstChar);
		if(minimalTypes != null) return minimalTypes.of(entity);
		return others.of(entity);
	}
}
