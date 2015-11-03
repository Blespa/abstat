package it.unimib.disco.summarization.test.unit;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import it.unimib.disco.summarization.export.WriteConceptToRDF;

import java.io.File;

import org.junit.Test;

public class WriteConceptToRDFTest extends TestWithTemporaryData{
	
	@Test
	public void shouldExportAsRDF() throws Exception {
		
		File inputFile = temporary.file("http://dbpedia.org/ontology/Artist##24##0.367985");
		File outputFile = temporary.file();
		
		WriteConceptToRDF.main(new String[]{
				inputFile.getAbsolutePath(),
				outputFile.getAbsolutePath(),
				"dataset",
				"dbpedia.org"
		});
		
		assertThat(outputFile.exists(), is(true));
	}
}
