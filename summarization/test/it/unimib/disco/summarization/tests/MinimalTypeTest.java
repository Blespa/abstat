package it.unimib.disco.summarization.tests;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;
import it.unimib.disco.summarization.utility.MinimalTypes;

import org.junit.Test;

public class MinimalTypeTest {

	@Test
	public void shouldParseAnEmptyContent() throws Exception {
		
		MinimalTypes minimalTypes = new MinimalTypes(new TextInputTestDouble());
		
		assertThat(minimalTypes.of("any").size(), equalTo(0));
	}
	
	@Test
	public void shouldReturnTheMinimalTypeOfAKnownEntity() throws Exception {
		
		TextInputTestDouble types = new TextInputTestDouble().withLine("1##http://entity##http://type");
		
		MinimalTypes minimalTypes = new MinimalTypes(types);
		
		assertThat(minimalTypes.of("http://entity").size(), equalTo(1));
	}
	
	@Test
	public void shouldReturnTheMinimalTypeSet() throws Exception {
		TextInputTestDouble types = new TextInputTestDouble().withLine("2##http://entity##http://type#-#http://other-type");
		
		MinimalTypes minimalTypes = new MinimalTypes(types);
		
		assertThat(minimalTypes.of("http://entity").size(), equalTo(2));
	}
}
