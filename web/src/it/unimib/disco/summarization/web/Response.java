package it.unimib.disco.summarization.web;

import java.io.InputStream;

public interface Response {

	public InputStream stream() throws Exception;

}