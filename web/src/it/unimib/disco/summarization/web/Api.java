package it.unimib.disco.summarization.web;

import java.io.InputStream;

public interface Api {

	public InputStream getResponseFromConnector(RequestParameters parameters) throws Exception;

}
