package it.unimib.disco.summarization.test.web;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import it.unimib.disco.summarization.test.unit.TemporaryFolder;
import it.unimib.disco.summarization.web.DeployedVersion;

import java.io.File;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DeployedVersionTest {
	
	private TemporaryFolder temporary = new TemporaryFolder();

	@Before
	public void setUp(){
		temporary.create();
	}
	
	@After
	public void tearDown(){
		temporary.delete();
	}
	
	@Test
	public void shouldGetTheCurrentBranchOfARepository() throws Exception {
		
		fill(newRepository(temporary.directory()));
		
		String branch = new DeployedVersion(temporary.directory()).branch();
		
		assertThat(branch, equalTo("master"));
	}
	
	@Test
	public void shouldGetTheCurrentTheCurrentCommit() throws Exception {
		
		fill(newRepository(temporary.directory()));
		
		String commit = new DeployedVersion(temporary.directory()).commit();
		
		assertThat(commit, notNullValue());
	}
	
	private Repository newRepository(File directory) throws Exception {
        return Git.init().setDirectory(directory).call().getRepository();
    }
	
	private void fill(Repository repository) throws Exception{
        Git repo = new Git(repository);
		repo.add()
            .addFilepattern("testfile")
            .call();
        repo.commit()
            .setMessage("Added testfile")
            .call();
        repo.close();
	}
}
