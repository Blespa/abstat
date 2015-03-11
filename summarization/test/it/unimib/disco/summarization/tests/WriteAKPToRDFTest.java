package it.unimib.disco.summarization.tests;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import it.unimib.disco.summarization.output.WriteAKPToRDF;

import java.io.File;

import org.junit.Test;

public class WriteAKPToRDFTest extends UnitTest{
	
	@Test
	public void shouldExportAsRDF() throws Exception {
		
		File inputFile = temporary.newFile("http://aaa.com/AAA##http://aaa.com/aaa##http://aaa.com/BBB##35##");
		File outputFile = temporary.newFile();
		
		WriteAKPToRDF.main(new String[]{
				inputFile.getAbsolutePath(),
				outputFile.getAbsolutePath(),
				"http://schemasummaries.org/dataset"
		});
		
		assertThat(outputFile.exists(), is(true));
	}
}
