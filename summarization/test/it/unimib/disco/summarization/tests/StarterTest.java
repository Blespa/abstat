package it.unimib.disco.summarization.tests;

import it.unimib.disco.summarization.starter.Starter;

import java.io.File;

import org.junit.Ignore;
import org.junit.Test;

public class StarterTest extends UnitTest{

	@Test
	@Ignore
	public void shouldBeAbleToProcessTheMusicOntology() {
		String ontologyDirectory = new File("test/it/unimib/disco/summarization/tests/").getAbsolutePath();
		
		Starter.main(new String[]{ontologyDirectory + "/", temporary.path() + "/", temporary.path() + "/"});
	}
}
