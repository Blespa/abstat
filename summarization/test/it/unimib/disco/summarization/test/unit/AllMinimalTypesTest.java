package it.unimib.disco.summarization.test.unit;

import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertThat;
import it.unimib.disco.summarization.dataset.AllMinimalTypes;

import org.junit.Test;

public class AllMinimalTypesTest extends TestWithTemporaryData{
	
	@Test
	public void entityWithKnownDBPediaType() throws Exception {
		
		temporary.namedFile("1##http://dbpedia.org/resource/Jasenica_(Valjevo)##type", "j_minType.txt");
		
		AllMinimalTypes types = new AllMinimalTypes(temporary.directory());
		
		assertThat(types.of("http://dbpedia.org/resource/Jasenica_(Valjevo)"), hasItem("type"));
	}
}
