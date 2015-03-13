package it.unimib.disco.summarization.tests;

import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertThat;
import it.unimib.disco.summarization.output.WriteConceptGraphToRDF;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

public class WriteConceptGraphToRDFTest extends UnitTest{

	@Test
	public void shouldTranslateFromSubclassTo() throws Exception {
		
		File subclasses = temporary.newFile("http://example.org/Guitar##http://example.org/Instrument");
		File broaderConcepts = temporary.newFile();
		
		WriteConceptGraphToRDF.main(new String[]{
				subclasses.getAbsolutePath(), broaderConcepts.getAbsolutePath(), "dataset"
		});
		
		assertThat(FileUtils.readLines(broaderConcepts), hasItem(
				"<http://ld-summaries.org/resource/dataset/example.org/Guitar> "
				+ "<http://www.w3.org/2004/02/skos/core#broader> "
				+ "<http://ld-summaries.org/resource/dataset/example.org/Instrument> ."));
		
	}
}
