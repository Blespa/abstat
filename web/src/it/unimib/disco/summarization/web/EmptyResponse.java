package it.unimib.disco.summarization.web;

import java.io.InputStream;

import org.apache.commons.io.IOUtils;

public class EmptyResponse implements Response{

	@Override
	public InputStream stream() throws Exception {
		return IOUtils.toInputStream("");
	}
}