package it.unimib.disco.summarization.tests;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import it.unimib.disco.summarization.utility.Pipeline;

import org.junit.Test;

public class PipelineTest {

	@Test
	public void shouldWrapAProcessing() throws Exception {
		
		ProcessingInspector inspector = new ProcessingInspector();
		
		new Pipeline(inspector).process(new TextInputTestDouble());
		
		assertThat(inspector.countProcessed(), equalTo(1));
	}
	
	@Test
	public void shouldWrapASetOfProcessings() throws Exception {
		ProcessingInspector[] processings = new ProcessingInspector[]{
				new ProcessingInspector(),
				new ProcessingInspector(),
		};
		
		new Pipeline(processings).process(new TextInputTestDouble());
		
		for(ProcessingInspector inspector : processings){
			assertThat(inspector.countProcessed(), equalTo(1));
		}
	}
}
