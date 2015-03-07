package it.unimib.disco.summarization.tests;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import it.unimib.disco.summarization.output.WriteConceptsTORDF;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;

public class WriteConceptsTORDFTest {

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
	public void shouldExportAsRDF() throws Exception {
		
		File inputFile = new File(temporary, "input.txt");
		FileUtils.write(inputFile, "http://dbpedia.org/ontology/Artist##24##0.367985");
		
		File outputFile = new File(temporary, "output.nt");
		
		WriteConceptsTORDF.main(new String[]{
				inputFile.getAbsolutePath(),
				outputFile.getAbsolutePath()
		});
		
		assertThat(outputFile.exists(), is(true));
	}
}
