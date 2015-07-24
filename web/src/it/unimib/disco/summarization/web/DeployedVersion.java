package it.unimib.disco.summarization.web;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;

public class DeployedVersion {

	private File repositoryFolder;

	public DeployedVersion(File directory) {
		this.repositoryFolder = directory;
	}

	public String branch() throws Exception {
		return repository().getBranch();
	}

	private Repository repository() throws IOException {
		return Git.open(repositoryFolder).getRepository();
	}

	public String commit() throws Exception {
		Git git = new Git(repository());
		String id = git.log().call().iterator().next().name();
		git.close();
		return id;
	}
}
