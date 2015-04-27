package it.unimib.disco.summarization.utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.hp.hpl.jena.vocabulary.OWL;


public class PartitionedMinimalTypes implements MinimalTypes {

	private HashMap<String, List<String>> types;

	public PartitionedMinimalTypes(InputFile types) throws Exception {
		this.types = buildMinimalTypes(types);
	}

	@Override
	public List<String> of(String entity) {
		List<String> result = types.get(entity);
		if(result == null){
			result = new ArrayList<String>();
			result.add(OWL.Thing.toString());
		}
		return result;
	}
	
	private HashMap<String, List<String>> buildMinimalTypes(InputFile types) throws Exception {
		HashMap<String, List<String>> minimalTypes = new HashMap<String, List<String>>();
		while(types.hasNextLine()){
			List<String> line = Arrays.asList(types.nextLine().replace("#-#", "##").split("##"));
			minimalTypes.put(line.get(1), line.subList(2, line.size()));
		}
		return minimalTypes;
	}
}
