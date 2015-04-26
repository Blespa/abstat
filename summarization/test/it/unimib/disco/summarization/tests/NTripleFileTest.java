package it.unimib.disco.summarization.tests;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import it.unimib.disco.summarization.utility.NTripleFile;

import org.junit.Test;

public class NTripleFileTest extends TestWithTemporaryData{

	@Test
	public void shouldProcessAnEmptyFile() throws Exception {
		
		NTripleAnalysisInspector analysis = new NTripleAnalysisInspector();
		
		new NTripleFile(analysis).process(temporary.file());
		
		assertThat(analysis.countProcessed(), equalTo(0));
	}
}