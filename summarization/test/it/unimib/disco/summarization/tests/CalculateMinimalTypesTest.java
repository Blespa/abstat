package it.unimib.disco.summarization.tests;

import static org.hamcrest.Matchers.hasSize;
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
		temporary.file("<http://instance1> <> <http://concept> .", "_types.nt");
		temporary.file("<http://instance2> <> <http://concept> .", "_types.nt");
		
		File subclasses = temporary.file("http://concept##http://thing");
		
		CalculateMinimalTypes.main(new String[]{
				temporary.path(),
				subclasses.getAbsolutePath(),
				temporary.path(),
				temporary.path()
		});
		
		assertThat(temporary.files("_minType.txt"), hasSize(2));
	}
}
