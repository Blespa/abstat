package it.unimib.disco.summarization.tests;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import it.unimib.disco.summarization.utility.DatatypeCount;
import it.unimib.disco.summarization.utility.NTripleFile;

import org.junit.Test;

public class NTripleFileTest extends TestWithTemporaryData{

	@Test
	public void shouldProcessAnEmptyFile() throws Exception {
		
		NTripleAnalysisInspector analysis = new NTripleAnalysisInspector();
		
		new NTripleFile(analysis).process(temporary.file());
		
		assertThat(analysis.countProcessed(), equalTo(0));
	}
	
	@Test
	public void shouldAnalyzeAWellFormedNTripleFile() throws Exception {
		
		NTripleAnalysisInspector analysis = new NTripleAnalysisInspector();
		
		new NTripleFile(analysis).process(temporary.file("<a> <b> <c> ."));
		
		assertThat(analysis.countProcessed(), equalTo(1));
	}
	
	@Test(timeout = 1000)
	public void shouldSkipStrangeLines() throws Exception {
		NTripleAnalysisInspector analysis = new NTripleAnalysisInspector();
		
		new NTripleFile(analysis)
				.process(temporary.file("曲：[http://musicbrainz.org/artist/a223958d-5c56-4b2c-a30a-87e357bc121b.html|周杰倫]"));
	
		assertThat(analysis.countProcessed(), equalTo(0));
	}
	
	@Test(timeout = 1000)
	public void shouldSkipOtherStrangeLines() throws Exception {
		NTripleAnalysisInspector analysis = new NTripleAnalysisInspector();
		
		new NTripleFile(analysis)
				.process(temporary.file(
						"Inaccurate ARs:" + "\n" +
						"" + "\n" +
						"    * 混音助理 means \"mixing assistant\", but is credited as \"co-mixer\".\" ."
						));
	
		assertThat(analysis.countProcessed(), equalTo(0));
	}
	
	@Test
	public void shouldIndexAlsoWithSpaces() throws Exception {
		NTripleAnalysisInspector analysis = new NTripleAnalysisInspector();
		
		new NTripleFile(analysis).process(temporary.file("<http://1234> <http://predicate> <http://uri with space> ."));
		
		assertThat(analysis.countProcessed(), equalTo(1));
	}
	
	@Test
	public void shouldProcessAString() throws Exception {
		DatatypeCount analysis = new DatatypeCount();
		
		new NTripleFile(analysis).process(temporary.file("<http://1234> <http://predicate> \"a string\" ."));
		
		assertThat(analysis.counts().size(), equalTo(1));
	}
}