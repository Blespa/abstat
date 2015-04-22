package it.unimib.disco.summarization.tests;

import it.unimib.disco.summarization.starter.Starter;

import java.io.File;

import org.junit.Ignore;
import org.junit.Test;

import com.hp.hpl.jena.vocabulary.RDFS;

public class StarterTest extends TestWithTemporaryData{

	@Test
	@Ignore
	public void shouldBeAbleToProcessTheMusicOntology() throws Exception {
		String ontologyDirectory = new File("test/it/unimib/disco/summarization/tests/").getAbsolutePath();
		
		Starter.main(new String[]{ontologyDirectory + "/", temporary.path() + "/", temporary.path() + "/"});
	}
	
	@Test
	public void shouldParseASmallExtractOfTheYAGO1Ontology() throws Exception {
		
		String wordnetEntity = "http://mpii.de/yago/resource/wordnet_entity_100001740";
		String wordnetPerson = "http://mpii.de/yago/resource/wordnet_person_100007846";
		String wordnetActorGeo = "http://mpii.de/yago/resource/wordnet_yagoActorGeo_1";
		String isLeaderOf = "http://mpii.de/yago/resource/isLeaderOf";
		
		ToyOntology ontology = new ToyOntology()
				.rdfs()
				.definingConcept(wordnetEntity)
				.definingResource(wordnetPerson)
					.aSubconceptOf(wordnetEntity)
				.definingResource(wordnetActorGeo)
					.aSubconceptOf(wordnetEntity)
				.definingResource(isLeaderOf)
					.thatHasProperty(RDFS.domain)
						.linkingTo(wordnetPerson)
					.thatHasProperty(RDFS.range)
						.linkingTo(wordnetActorGeo);
		
		temporary.file(ontology.serialize(), "owl");
		
		Starter.main(new String[]{temporary.path() + "/", temporary.path() + "/", temporary.path() + "/"});
	}
}
