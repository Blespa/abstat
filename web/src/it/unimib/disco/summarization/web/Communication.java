package it.unimib.disco.summarization.web;

import java.io.IOException;
import java.io.InputStream;

public interface Communication {

	void setContentType(String contentType);

	void setOutputStream(InputStream content) throws IOException;

	void setHandled();

	String getParameter(String string);

}
