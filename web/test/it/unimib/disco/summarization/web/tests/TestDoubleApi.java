package it.unimib.disco.summarization.web.tests;

import java.io.InputStream;

import org.apache.commons.io.IOUtils;

import it.unimib.disco.summarization.web.Api;
import it.unimib.disco.summarization.web.Communication;

public class TestDoubleApi implements Api {

	@Override
	public InputStream getAutocomplete(Communication communication) throws Exception {
		return IOUtils.toInputStream("\"response\": {" + 
											"\"docs\": [{" +
													"\"URI\":[\"http://dbpedia.org/ontology/City\"]}" +
											"]" + 
									"}");
	}
}
