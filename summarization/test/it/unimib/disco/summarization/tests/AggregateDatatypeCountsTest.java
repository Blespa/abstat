package it.unimib.disco.summarization.tests;

import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.*;
import it.unimib.disco.summarization.output.AggregateDatatypeCounts;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import com.hp.hpl.jena.vocabulary.RDFS;

public class AggregateDatatypeCountsTest extends TestWithTemporaryData{

	@Test
	public void shouldCountAndAggregateDatatypeOccurrences() throws Exception {
		
		temporary.namedFile("entity##property##\"35\"##type", "a_dt_properties.nt");
		temporary.namedFile("entity##property##\"82\"##type", "b_dt_properties.nt");
		temporary.namedFile("entity##property##\"35\"", "c_dt_properties.nt");
		temporary.namedFile("entity##property##\"a string\"", "d_dt_properties.nt");
		
		AggregateDatatypeCounts.main(new String[]{
			temporary.directory().getAbsolutePath(),
			temporary.directory().getAbsolutePath(),
		});
		
		List<String> lines = linesOf("count-datatype.txt");
		assertThat(lines, hasItem("type##2"));
		assertThat(lines, hasItem(RDFS.Literal.getURI() + "##2"));
	}
	
	private List<String> linesOf(String file) throws Exception {
		return FileUtils.readLines(new File(temporary.directory(), file));
	}
}
