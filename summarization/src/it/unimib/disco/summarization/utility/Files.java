package it.unimib.disco.summarization.utility;

import java.io.File;
import java.io.FilenameFilter;

public class Files{
	
	public File[] get(File typesDirectory, final String suffix) {
		File[] files = typesDirectory.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(suffix);
			}
		});
		return files;
	}
}