package it.unimib.disco.summarization.test.unit;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;
import it.unimib.disco.summarization.dataset.PropertyCount;

import org.junit.Test;

public class PropertyCountTest {

	@Test
	public void shouldCountAnEmptySetOfTriples() {
		PropertyCount propertyCount = new PropertyCount();
		
		assertThat(propertyCount.counts().size(), equalTo(0));
	}
	
	@Test
	public void shouldCountATriple() throws Exception {
		PropertyCount propertyCount = new PropertyCount();
		
		propertyCount.track(new TripleBuilder().withProperty("http://property").asTriple());
		
		assertThat(propertyCount.counts().get("http://property"), equalTo(1l));
	}
	
	@Test
	public void shouldCountManyTriples() throws Exception {
		PropertyCount propertyCount = new PropertyCount();
		
		propertyCount.track(new TripleBuilder().withProperty("http://property").asTriple())
					 .track(new TripleBuilder().withProperty("http://property").asTriple());
		
		assertThat(propertyCount.counts().get("http://property"), equalTo(2l));
	}
}
