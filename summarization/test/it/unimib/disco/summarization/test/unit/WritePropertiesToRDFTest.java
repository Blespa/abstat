package it.unimib.disco.summarization.test.unit;

import it.unimib.disco.summarization.export.WriteDatatypePropertyToRDF;

import java.io.File;

import org.junit.Test;

public class WritePropertiesToRDFTest extends TestWithTemporaryData{

	@Test
	public void shouldParseAFullLine() throws Exception {
		File input = temporary.file("http://dbpedia.org/ontology/operatingSystem##2##0##0##0##0##0##0");
		File output = temporary.file();
		
		WriteDatatypePropertyToRDF.main(new String[]{input.getAbsolutePath(), output.getAbsolutePath(), "dataset", "dbpedia.org",});
	}

	@Test
	public void shouldParseAPartialLine() throws Exception {
		File input = temporary.file("http://dbpedia.org/ontology/operatingSystem##2##0##0##0##0");
		File output = temporary.file();
		
		WriteDatatypePropertyToRDF.main(new String[]{input.getAbsolutePath(), output.getAbsolutePath(), "dataset", "dbpedia.org",});
	}
}
