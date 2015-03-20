package it.unimib.disco.summarization.tests;

import it.unimib.disco.summarization.starter.Starter;

import java.io.File;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

public class StarterTest extends UnitTest{

	@Test
	public void shouldBeAbleToProcessTheMusicOntology() {
		String ontologyDirectory = new File("test/it/unimib/disco/summarization/tests/").getAbsolutePath();
		
		Starter.main(new String[]{ontologyDirectory + "/", temporary.path() + "/", temporary.path() + "/"});
	}
	
	@Test
	public void shouldParseASmallExtractOfTheYAGO1Ontology() throws Exception {
		
		String[] ontology = new String[]{
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>",
				"<rdf:RDF",
					"xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">",
					"<rdf:Description rdf:about=\"http://mpii.de/yago/resource/wordnet_entity_100001740\">",
						"<rdf:type rdf:resource=\"http://www.w3.org/2000/01/rdf-schema#Class\"/>",
					"</rdf:Description>",
					"<rdf:Description rdf:about=\"http://mpii.de/yago/resource/wordnet_person_100007846\">",
						"<subClassOf xmlns=\"http://www.w3.org/2000/01/rdf-schema#\" rdf:resource=\"http://mpii.de/yago/resource/wordnet_entity_100001740\"/>",
					"</rdf:Description>",
					"<rdf:Description rdf:about=\"http://mpii.de/yago/resource/wordnet_yagoActorGeo_1\">",
						"<subClassOf xmlns=\"http://www.w3.org/2000/01/rdf-schema#\" rdf:resource=\"http://mpii.de/yago/resource/wordnet_entity_100001740\"/>",
					"</rdf:Description>",
					"<rdf:Description rdf:about=\"http://mpii.de/yago/resource/isLeaderOf\">",
						"<domain xmlns=\"http://www.w3.org/2000/01/rdf-schema#\" rdf:resource=\"http://mpii.de/yago/resource/wordnet_person_100007846\"/>",
					"</rdf:Description>",
					"<rdf:Description rdf:about=\"http://mpii.de/yago/resource/isLeaderOf\">",
						"<range xmlns=\"http://www.w3.org/2000/01/rdf-schema#\" rdf:resource=\"http://mpii.de/yago/resource/wordnet_yagoActorGeo_1\"/>",
					"</rdf:Description>",
				"</rdf:RDF>"
		};
		
		temporary.newFile(StringUtils.join(ontology, "\n"), "owl");
		
		Starter.main(new String[]{temporary.path() + "/", temporary.path() + "/", temporary.path() + "/"});
	}
}
