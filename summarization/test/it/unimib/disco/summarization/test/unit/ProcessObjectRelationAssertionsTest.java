package it.unimib.disco.summarization.test.unit;

import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertThat;
import it.unimib.disco.summarization.export.ProcessObjectRelationAssertions;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

public class ProcessObjectRelationAssertionsTest extends TestWithTemporaryData{

	@Test
	public void shouldCountAndAggregatePropertiesOccurrences() throws Exception {
		temporary.namedFile("", "others_minType.txt");
		
		temporary.namedFile("1##http://a##http://concept", "a_minType.txt");
		temporary.namedFile("1##http://b##http://other-concept", "b_minType.txt");
		temporary.namedFile("1##http://c##http://concept", "c_minType.txt");
		temporary.namedFile("1##http://d##http://other-concept", "d_minType.txt");
		
		temporary.namedFile("http://a##http://property2##http://b", "a_obj_properties.nt");
		temporary.namedFile("http://b##http://property1##http://c", "b_obj_properties.nt");
		temporary.namedFile("http://c##http://property2##http://d", "c_obj_properties.nt");
		
		ProcessObjectRelationAssertions.main(new String[]{
			temporary.directory().getAbsolutePath(),
			temporary.directory().getAbsolutePath(),
			temporary.directory().getAbsolutePath(),
		});
		
		List<String> properties = linesOf("count-object-properties.txt");
		assertThat(properties, hasItem("http://property1##1"));
		assertThat(properties, hasItem("http://property2##2"));
		
		List<String> akps = linesOf("object-akp.txt");
		assertThat(akps, hasItem("http://concept##http://property2##http://other-concept##2"));
		assertThat(akps, hasItem("http://other-concept##http://property1##http://concept##1"));
	}
	
	private List<String> linesOf(String file) throws Exception {
		return FileUtils.readLines(new File(temporary.directory(), file));
	}
}
