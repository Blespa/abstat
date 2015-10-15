package it.unimib.disco.summarization.test.unit;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import it.unimib.disco.summarization.dataset.FileSystemConnector;
import it.unimib.disco.summarization.dataset.InputFile;
import it.unimib.disco.summarization.dataset.TextInput;
import it.unimib.disco.summarization.dataset.TextOutput;

import java.io.File;

import org.junit.Test;


public class TextInputTest extends TestWithTemporaryData{

	@Test
	public void contentOfAnEmptyFileShouldBeNone() throws Exception {
		new TextOutput(connector()).close();
		
		InputFile text = new TextInput(connector());
		
		assertThat(text.hasNextLine(), is(false));
	}

	@Test
	public void shouldReadTheLines() throws Exception {
		new TextOutput(connector()).writeLine("content").close();
		
		InputFile textInput = new TextInput(connector());
		
		assertThat(textInput.nextLine(), is(equalTo("content")));
	}
	
	private FileSystemConnector connector() {
		return new FileSystemConnector(new File(temporary.path(), "any"));
	}
}
