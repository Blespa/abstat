package it.unimib.disco.summarization.test.unit;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.*;
import it.unimib.disco.summarization.dataset.ConceptCount;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

public class ConceptCountTest extends TestWithTemporaryData{

	@Test
	public void shouldAggregateTheConceptCounts() throws Exception {
		File results = temporary.file();
		
		new ConceptCount().process(temporary.file("concept#name##10"
												+ "\n"
												+ "other concept##34"))
						  .process(temporary.file("concept#name##23"
												+ "\n"
												+ "other concept##4"))
						  .writeResultsTo(results);
		
		List<String> lines = linesOf(results);
		assertThat(lines, hasSize(2));
		assertThat(lines, hasItem("concept#name##33"));
		assertThat(lines, hasItem("other concept##38"));
	}
	
	@Test
	public void shouldOmitConceptsWithNoOccurrences() throws Exception {
		File results = temporary.file();
		
		new ConceptCount().process(temporary.file("concept##0"))
						  .writeResultsTo(results);
		
		assertThat(linesOf(results), hasSize(0));
	}

	private List<String> linesOf(File file) throws Exception {
		return FileUtils.readLines(file);
	}
}
