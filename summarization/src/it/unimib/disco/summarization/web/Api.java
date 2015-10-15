package it.unimib.disco.summarization.web;

import java.io.InputStream;

public interface Api {

	public InputStream get(RequestParameters parameters) throws Exception;
}
