package it.unimib.disco.summarization.test.web;

import java.io.InputStream;
import java.util.HashMap;

import org.apache.commons.io.IOUtils;

import it.unimib.disco.summarization.web.Connector;
import it.unimib.disco.summarization.web.QueryString;

public class ConnectorTestDouble implements Connector {

	private HashMap<String, String> suggestions;

	public ConnectorTestDouble() {
		this.suggestions = suggestions();
	}
	
	@Override
	public InputStream query(String path, QueryString queryString) throws Exception {
		return IOUtils.toInputStream(this.suggestions.get(path));
	}

	private HashMap<String, String> suggestions() {
		HashMap<String, String> suggestions = new HashMap<String, String>();
		suggestions.put("/solr/indexing/concept-suggest", response("http://dbpedia.org/ontology/City"));
		suggestions.put("/solr/indexing/property-suggest", response("http://dbpedia.org/ontology/birthDate"));
		return suggestions;
	}

	private String response(String string) {
		return "\"response\": {" + 
						"\"docs\": [{" +
								"\"URI\":[\"" + string + "\"]}" +
						"]" + 
				"}";
	}
}
