package it.unimib.disco.summarization.tests;

import it.unimib.disco.summarization.starter.Starter;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;

public class StarterTest {

	File temporary = new File("tmp");
	
	@Rule
    public final ExpectedSystemExit exit = ExpectedSystemExit.none();
	
	@Before
	public void eraseTemporaryFolder(){
		temporary.mkdir();
	}
	
	@After
	public void deleteTemporaryFolders(){
		FileUtils.deleteQuietly(temporary);
	}
	
	@Test
	public void shouldBeAbleToProcessTheMusicOntology() {
		String ontologyDirectory = new File("test/it/unimib/disco/summarization/tests/").getAbsolutePath();
		
		Starter.main(new String[]{ontologyDirectory, temporary.getAbsolutePath(), temporary.getAbsolutePath()});
	}
}
