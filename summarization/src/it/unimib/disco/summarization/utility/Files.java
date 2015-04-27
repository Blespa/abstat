package it.unimib.disco.summarization.utility;

import java.io.File;
import java.io.FilenameFilter;

import org.apache.commons.lang3.StringUtils;

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
	
	public String prefixOf(InputFile types) {
		String[] splitted = StringUtils.split(new File(types.name()).getName(), "_");
		return splitted.length == 1 ? "_": splitted[0];
	}
}