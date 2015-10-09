package it.unimib.disco.summarization.web;

import java.io.InputStream;

public interface Api {

	InputStream getAutocomplete(Communication communication) throws Exception;

}
