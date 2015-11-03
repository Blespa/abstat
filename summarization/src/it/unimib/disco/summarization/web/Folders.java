package it.unimib.disco.summarization.web;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Folders {

	private File root;

	public Folders(File directory) {
		this.root = directory;
	}

	public List<String> children() {
		ArrayList<String> filtered = new ArrayList<String>();
		for(File file : root.listFiles()){
			if(file.isDirectory()) filtered.add(file.getName());
		}
		return filtered;
	}
}
