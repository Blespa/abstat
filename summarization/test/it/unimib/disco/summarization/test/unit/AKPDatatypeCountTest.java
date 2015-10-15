package it.unimib.disco.summarization.test.unit;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;
import it.unimib.disco.summarization.dataset.AKPDatatypeCount;

import org.junit.Test;

import com.hp.hpl.jena.vocabulary.RDFS;

public class AKPDatatypeCountTest {

	@Test
	public void emptyContent() throws Exception {
		
		AKPDatatypeCount count = new AKPDatatypeCount(new TextInputTestDouble());
		
		assertThat(count.counts().size(), equalTo(0));
	}

	@Test
	public void oneSingleNotTypedAKPWithOneSingleMinimalType() throws Exception {
		
		TextInputTestDouble types = new TextInputTestDouble().withLine("1##entity##type");
		
		AKPDatatypeCount count = new AKPDatatypeCount(types).track(new TripleBuilder()
																			.withSubject("entity")
																			.withProperty("property")
																			.withLiteral("a string")
																		.asTriple());
		
		assertThat(count.counts().get("type##property##" + RDFS.Literal), equalTo(1l));
	}
	
	@Test
	public void oneSingleNotTypedAKPWithManyMinimalTypes() throws Exception {
		
		TextInputTestDouble types = new TextInputTestDouble().withLine("1##entity##type#-#other");
		
		AKPDatatypeCount count = new AKPDatatypeCount(types).track(new TripleBuilder()
																			.withSubject("entity")
																			.withProperty("property")
																			.withLiteral("a string")
																		.asTriple());
		
		assertThat(count.counts().get("other##property##" + RDFS.Literal), equalTo(1l));
	}
	
	@Test
	public void oneSingleTypedAKP() throws Exception {
		
		TextInputTestDouble types = new TextInputTestDouble().withLine("1##entity##type#-#other");
		
		AKPDatatypeCount count = new AKPDatatypeCount(types).track(new TripleBuilder()
																			.withSubject("entity")
																			.withProperty("property")
																			.withTypedLiteral("34", "type")
																		.asTriple());
		
		assertThat(count.counts().get("type##property##type"), equalTo(1l));
	}
}
