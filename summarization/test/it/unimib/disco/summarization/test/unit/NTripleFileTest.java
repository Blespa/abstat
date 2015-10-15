package it.unimib.disco.summarization.test.unit;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import it.unimib.disco.summarization.dataset.DatatypeCount;
import it.unimib.disco.summarization.dataset.NTripleFile;

import org.junit.Test;

public class NTripleFileTest extends TestWithTemporaryData{

	@Test
	public void shouldProcessAnEmptyFile() throws Exception {
		
		NTripleAnalysisInspector analysis = new NTripleAnalysisInspector();
		
		new NTripleFile(analysis).process(temporary.fileTextInput());
		
		assertThat(analysis.countProcessed(), equalTo(0));
	}
	
	@Test
	public void shouldAnalyzeAWellFormedNTripleFile() throws Exception {
		
		NTripleAnalysisInspector analysis = new NTripleAnalysisInspector();
		
		new NTripleFile(analysis).process(temporary.fileTextInput("a##b##c"));
		
		assertThat(analysis.countProcessed(), equalTo(1));
	}
	
	@Test
	public void shouldIndexAlsoWithSpaces() throws Exception {
		NTripleAnalysisInspector analysis = new NTripleAnalysisInspector();
		
		new NTripleFile(analysis).process(temporary.fileTextInput("http://1234##http://predicate##http://uri with space"));
		
		assertThat(analysis.countProcessed(), equalTo(1));
	}
	
	@Test
	public void shouldProcessAStringWithLanguage() throws Exception {
		DatatypeCount analysis = new DatatypeCount();
		
		new NTripleFile(analysis).process(temporary.fileTextInput("http://1234##http://predicate##\"a string@en\""));
		
		assertThat(analysis.counts().size(), equalTo(1));
	}
	
	@Test
	public void shouldProcessAString() throws Exception {
		DatatypeCount analysis = new DatatypeCount();
		
		new NTripleFile(analysis).process(temporary.fileTextInput("http://1234##http://predicate##\"a string\""));
		
		assertThat(analysis.counts().size(), equalTo(1));
	}
	
	@Test
	public void shouldProcessADatatype() throws Exception {
		DatatypeCount analysis = new DatatypeCount();
		
		new NTripleFile(analysis).process(temporary.fileTextInput("http://1234##http://predicate##\"34\"##type"));
		
		assertThat(analysis.counts().size(), equalTo(1));
	}
	
	@Test
	public void shouldProcessAComplexDatatype() throws Exception {
		DatatypeCount analysis = new DatatypeCount();
		
		new NTripleFile(analysis).process(temporary.fileTextInput("http://1234##http://predicate##\"34\"##http://uri#type"));
		
		assertThat(analysis.counts().get("http://uri#type"), equalTo(1l));
	}
}