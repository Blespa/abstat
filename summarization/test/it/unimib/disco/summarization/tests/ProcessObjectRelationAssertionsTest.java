package it.unimib.disco.summarization.tests;

import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertThat;
import it.unimib.disco.summarization.output.ProcessObjectRelationAssertions;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

public class ProcessObjectRelationAssertionsTest extends TestWithTemporaryData{

	@Test
	public void shouldCountAndAggregatePropertiesOccurrences() throws Exception {
		
		temporary.namedFile("entity##http://property1##other", "a_obj_properties.nt");
		temporary.namedFile("entity##http://property1##other", "b_obj_properties.nt");
		temporary.namedFile("entity##http://property2##other", "c_obj_properties.nt");
		temporary.namedFile("entity##http://property2##other", "d_obj_properties.nt");
		
		ProcessObjectRelationAssertions.main(new String[]{
			temporary.directory().getAbsolutePath(),
			temporary.directory().getAbsolutePath(),
		});
		
		List<String> properties = linesOf("count-object-properties.txt");
		assertThat(properties, hasItem("http://property1##2"));
		assertThat(properties, hasItem("http://property2##2"));
	}
	
	private List<String> linesOf(String file) throws Exception {
		return FileUtils.readLines(new File(temporary.directory(), file));
	}
}
