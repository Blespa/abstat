package it.unimib.disco.summarization.utility;

public interface OutputFile {

	OutputFile writeLine(String content) throws Exception;

	void close() throws Exception;
}	
