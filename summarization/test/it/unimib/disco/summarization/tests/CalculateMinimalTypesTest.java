package it.unimib.disco.summarization.tests;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import it.unimib.disco.summarization.output.CalculateMinimalTypes;

import java.io.File;

import org.junit.Test;

public class CalculateMinimalTypesTest extends TestWithTemporaryData{

	@Test
	public void shouldParseManyFiles() throws Exception {
		
		temporary.file(new ToyOntology()
								.owl()
								.definingConcept("http://concept")
									.aSubconceptOf("http://thing")
								.serialize(), "owl");
		temporary.namedFile("http://instance1##type##http://concept", "0_types.nt");
		temporary.namedFile("http://instance2##type##http://concept", "1_types.nt");
		
		File subclasses = temporary.file("http://concept##http://thing");
		File concepts = temporary.file("http://concept\nhttp://thing");
		
		CalculateMinimalTypes.main(new String[]{
				temporary.path(),
				subclasses.getAbsolutePath(),
				concepts.getAbsolutePath(),
				temporary.path(),
				temporary.path()
		});
		
		assertThat(temporary.files("_minType.txt").length, equalTo(2));
	}
}
