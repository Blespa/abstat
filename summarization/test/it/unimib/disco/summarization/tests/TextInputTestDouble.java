package it.unimib.disco.summarization.tests;

import it.unimib.disco.summarization.utility.InputFile;

public class TextInputTestDouble implements InputFile{

	@Override
	public String name() {
		return "the_name";
	}

	@Override
	public String nextLine() throws Exception {
		return "";
	}

	@Override
	public boolean hasNextLine() {
		return false;
	}

	@Override
	public InputFile reopen() throws Exception {
		return this;
	}
}