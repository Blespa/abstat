package it.unimib.disco.summarization.utility;

public interface InputFile {
	
	String nextLine() throws Exception;
	
	boolean hasNextLine();
}
