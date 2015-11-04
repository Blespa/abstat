package it.unimib.disco.summarization.test.unit;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import it.unimib.disco.summarization.export.WriteAKPToRDF;

import java.io.File;

import org.junit.Test;

public class WriteAKPToRDFTest extends TestWithTemporaryData{
	
	@Test
	public void shouldExportAsRDF() throws Exception {
		
		File inputFile = temporary.file("http://aaa.com/AAA##http://aaa.com/aaa##http://aaa.com/BBB##35##");
		File outputFile = temporary.file();
		
		WriteAKPToRDF.main(new String[]{
				inputFile.getAbsolutePath(),
				outputFile.getAbsolutePath(),
				"http://schemasummaries.org/dataset",
				"dbpedia.org",
				"object"
		});
		
		assertThat(outputFile.exists(), is(true));
	}
}
