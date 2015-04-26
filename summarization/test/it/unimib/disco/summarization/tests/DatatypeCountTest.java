package it.unimib.disco.summarization.tests;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import it.unimib.disco.summarization.utility.DatatypeCount;

import org.junit.Test;

import com.hp.hpl.jena.vocabulary.RDFS;

public class DatatypeCountTest extends TestWithTemporaryData{

	@Test
	public void emptyContent() throws Exception {
		
		DatatypeCount datatypeCount = new DatatypeCount();
		
		assertThat(datatypeCount.counts().size(), equalTo(0));
	}
	
	@Test
	public void shouldTrackADatatypeTriple() throws Exception {
		
		DatatypeCount datatypeCount = new DatatypeCount();
		
		datatypeCount.track(new TripleBuilder().withObject("\"35\"^^<type>").asTriple());
		
		assertThat(datatypeCount.counts().get("type"), equalTo(1l));
	}
	
	@Test
	public void shouldTrackAnUnknownDatatype() throws Exception {
		
		DatatypeCount datatypeCount = new DatatypeCount();
		
		datatypeCount.track(new TripleBuilder().withObject("\"any string\"").asTriple());
		
		assertThat(datatypeCount.counts().get(RDFS.Literal.getURI()), equalTo(1l));
	}
	
	@Test
	public void shouldTrackManyOccurrences() throws Exception {
		
		DatatypeCount datatypeCount = new DatatypeCount();
		
		datatypeCount.track(new TripleBuilder().withObject("\"35\"").asTriple())
					 .track(new TripleBuilder().withObject("\"35\"").asTriple());
		
		assertThat(datatypeCount.counts().get(RDFS.Literal.getURI()), equalTo(2l));
	}
}
