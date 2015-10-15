package it.unimib.disco.summarization.test.unit;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import it.unimib.disco.summarization.dataset.Files;

import org.junit.Test;

public class FilesTest {

	@Test
	public void shouldGiveTheRightPrefix() {
		
		TextInputTestDouble file = new TextInputTestDouble().withName("a_aaa.txt");
		
		String prefix = new Files().prefixOf(file);
		
		assertThat(prefix, equalTo("a"));
	}
	
	@Test
	public void shouldGiveTheRightPrefixOnCollidingChars() {
		
		TextInputTestDouble file = new TextInputTestDouble().withName("__aaa.txt");
		
		String prefix = new Files().prefixOf(file);
		
		assertThat(prefix, equalTo("_"));
	}
	
	@Test
	public void shouldGiveTheRightPrefixOnCollidingCharsAndLongName() {
		
		TextInputTestDouble file = new TextInputTestDouble().withName("/home/b/c/__aaa_b.txt");
		
		String prefix = new Files().prefixOf(file);
		
		assertThat(prefix, equalTo("_"));
	}
}
