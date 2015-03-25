package it.unimib.disco.summarization.tests;

import it.unimib.disco.summarization.output.WriteDatatypePropertyToRDF;

import java.io.File;

import org.junit.Test;

public class WritePropertiesToRDFTest extends UnitTest{

	@Test
	public void shouldParseAFullLine() throws Exception {
		File input = temporary.newFile("http://dbpedia.org/ontology/operatingSystem##2##0##0##0##0##0##0");
		File output = temporary.newFile();
		
		WriteDatatypePropertyToRDF.main(new String[]{input.getAbsolutePath(), output.getAbsolutePath(), "dataset"});
	}

	@Test
	public void shouldParseAPartialLine() throws Exception {
		File input = temporary.newFile("http://dbpedia.org/ontology/operatingSystem##2##0##0##0##0");
		File output = temporary.newFile();
		
		WriteDatatypePropertyToRDF.main(new String[]{input.getAbsolutePath(), output.getAbsolutePath(), "dataset"});
	}
}
