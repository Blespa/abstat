package it.unimib.disco.summarization.web;

import java.io.InputStream;

public interface Connector {

	InputStream query(String path, QueryString queryString) throws Exception;

}
