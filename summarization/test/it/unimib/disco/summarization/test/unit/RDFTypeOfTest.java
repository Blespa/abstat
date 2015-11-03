package it.unimib.disco.summarization.test.unit;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;
import it.unimib.disco.summarization.ontology.RDFTypeOf;

import org.junit.Test;

import com.hp.hpl.jena.rdf.model.Resource;

public class RDFTypeOfTest {

	@Test
	public void aResourceShouldBeExternalOnDifferentDomainName() {
		
		Resource type = new RDFTypeOf("dbpedia.org").resource("http://schema.org/City");
		
		assertThat(type.getURI(), containsString("External"));
	}

	@Test
	public void anObjectAKPShouldBeExternalOnExternalObject() {
		
		Resource type = new RDFTypeOf("dbpedia.org").objectAKP("http://dbpedia.org/City", "http://schema.org/City");
		
		assertThat(type.getURI(), containsString("External"));
	}
	
	@Test
	public void aDatatypeAKPShouldBeExternalOnExternalSubject() {
		
		Resource type = new RDFTypeOf("dbpedia.org").datatypeAKP("http://schema.org/City");
		
		assertThat(type.getURI(), containsString("External"));
	}
}
