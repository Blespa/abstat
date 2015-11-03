package it.unimib.disco.summarization.web;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class Folders {

	private File root;

	public Folders(File directory) {
		this.root = directory;
	}

	public List<String> children() {
		return Arrays.asList(root.list());
	}
}
