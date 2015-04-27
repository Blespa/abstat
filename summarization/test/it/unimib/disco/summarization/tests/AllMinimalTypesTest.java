package it.unimib.disco.summarization.tests;

import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertThat;
import it.unimib.disco.summarization.utility.AllMinimalTypes;

import org.junit.Test;

public class AllMinimalTypesTest extends TestWithTemporaryData{

	@Test
	public void entityWithKnownType() throws Exception {
		
		temporary.namedFile("1##http://a##type", "a_minType.txt");
		
		AllMinimalTypes types = new AllMinimalTypes(temporary.directory());
		
		assertThat(types.of("http://a"), hasItem("type"));
	}
}
