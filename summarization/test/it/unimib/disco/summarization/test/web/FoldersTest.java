package it.unimib.disco.summarization.test.web;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.*;
import it.unimib.disco.summarization.test.unit.TemporaryFolder;
import it.unimib.disco.summarization.web.Folders;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class FoldersTest {

	private TemporaryFolder temp;

	@Before
	public void createTemporaryFolder(){
		this.temp = new TemporaryFolder().create();
	}
	
	@After
	public void deleteTemporaryFolder(){
		this.temp.delete();
	}
	
	@Test
	public void onAnEmptyDirectoryShouldGiveEmptySet() {
		
		Folders folder = new Folders(temp.directory());
		
		assertThat(folder.children(), empty());
	}
	
	@Test
	public void shouldGetTheContents() throws Exception {
		
		temp.add("aaa").add("bbb");
		
		Folders folder = new Folders(temp.directory());
		
		assertThat(folder.children(), hasSize(2));
	}
	
	@Test
	public void shouldSkipFilesAndKeepDirectoriesOnly() throws Exception {
		
		temp.file();
		
		Folders folder = new Folders(temp.directory());
		
		assertThat(folder.children(), empty());
	}
}
