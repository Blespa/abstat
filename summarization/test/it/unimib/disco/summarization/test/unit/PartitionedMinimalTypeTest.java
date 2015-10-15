package it.unimib.disco.summarization.test.unit;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;
import it.unimib.disco.summarization.dataset.MinimalTypes;
import it.unimib.disco.summarization.dataset.PartitionedMinimalTypes;

import org.junit.Test;

import com.hp.hpl.jena.vocabulary.OWL;

public class PartitionedMinimalTypeTest {

	@Test
	public void shouldParseAnEmptyContent() throws Exception {
		
		MinimalTypes minimalTypes = new PartitionedMinimalTypes(new TextInputTestDouble());
		
		assertThat(minimalTypes.of("any").get(0), equalTo(OWL.Thing.toString()));
	}
	
	@Test
	public void shouldReturnTheMinimalTypeOfAKnownEntity() throws Exception {
		
		TextInputTestDouble types = new TextInputTestDouble().withLine("1##http://entity##http://type");
		
		MinimalTypes minimalTypes = new PartitionedMinimalTypes(types);
		
		assertThat(minimalTypes.of("http://entity").size(), equalTo(1));
	}
	
	@Test
	public void shouldReturnTheMinimalTypeSet() throws Exception {
		TextInputTestDouble types = new TextInputTestDouble().withLine("2##http://entity##http://type#-#http://other-type");
		
		MinimalTypes minimalTypes = new PartitionedMinimalTypes(types);
		
		assertThat(minimalTypes.of("http://entity").size(), equalTo(2));
	}
}
