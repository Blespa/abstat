package it.unimib.disco.summarization.web;

import java.io.File;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.server.Request;

public class FileResponse implements Response{
	
	private File file;

	public FileResponse(String file){
		this.file = new File("views/" + file);
	}

	@Override
	public void sendResponse(Request base, HttpServletResponse response, RequestParameters parameters) throws Exception {
		IOUtils.copy(FileUtils.openInputStream(file), response.getOutputStream());
		base.setHandled(true);
	}
}