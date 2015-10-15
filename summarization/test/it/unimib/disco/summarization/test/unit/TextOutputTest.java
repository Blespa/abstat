package it.unimib.disco.summarization.test.unit;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import it.unimib.disco.summarization.dataset.FileSystemConnector;
import it.unimib.disco.summarization.dataset.TextOutput;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

public class TextOutputTest extends TestWithTemporaryData{

	@Test
	public void shouldWriteStrings() throws Exception {
		
		File temporaryFile = temporary.file();
		
		TextOutput textFile = new TextOutput(new FileSystemConnector(temporaryFile));
		
		textFile.writeLine("the line").close();
		
		assertThat(FileUtils.readLines(temporaryFile), hasSize(1));
	}
}
