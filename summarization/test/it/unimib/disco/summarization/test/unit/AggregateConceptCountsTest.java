package it.unimib.disco.summarization.test.unit;

import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertThat;
import it.unimib.disco.summarization.export.AggregateConceptCounts;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

public class AggregateConceptCountsTest extends TestWithTemporaryData {

	@Test
	public void shouldAggregateTheConceptCounts() throws Exception {

		temporary.namedFile("concept#name##10" + "\n" + "other concept##34", "a_countConcepts.txt");
		temporary.namedFile("concept#name##23" + "\n" + "other concept##4", "b_countConcepts.txt");
		
		AggregateConceptCounts.main(new String[]{
				temporary.directory().getAbsolutePath(),
				temporary.directory().getAbsolutePath()
		});
		
		assertThat(linesOf("count-concepts.txt"), hasItem("concept#name##33"));
	}

	private List<String> linesOf(String file) throws Exception {
		return FileUtils.readLines(new File(temporary.directory(), file));
	}
}
