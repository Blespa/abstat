package it.unimib.disco.summarization.utility;

public interface InputFile {

	public String name();

	public String nextLine() throws Exception;

	public boolean hasNextLine();

	public InputFile reopen() throws Exception;
}