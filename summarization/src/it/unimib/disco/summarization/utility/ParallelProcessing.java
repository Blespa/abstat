package it.unimib.disco.summarization.utility;

import it.unimib.disco.summarization.output.Events;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ParallelProcessing{
	
	private File sourceDirectory;
	private String suffix;

	public ParallelProcessing(File directory, String suffix) {
		this.sourceDirectory = directory;
		this.suffix = suffix;
	}
	
	public void process(final Processing processing) {
		ExecutorService executor = Executors.newFixedThreadPool(10);
		for(final File file : new Files().get(sourceDirectory, suffix)){
			executor.execute(new Runnable() {
				@Override
				public void run() {
					try {
						processing.process(new TextInput(new FileSystemConnector(file)));
					} catch (Exception e) {
						new Events().error(file, e);
					}
				}
			});
		}
		executor.shutdown();
	    while(!executor.isTerminated()){}
	}
}