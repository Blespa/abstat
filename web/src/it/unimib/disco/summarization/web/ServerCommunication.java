package it.unimib.disco.summarization.web;

import it.unimib.disco.summarization.web.tests.ClientCommunication;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.server.Request;

public class ServerCommunication implements Communication{

	private HttpServletRequest request;
	private HttpServletResponse response;
	private Request base;

	public ServerCommunication(HttpServletRequest request, HttpServletResponse response, Request base) {
		this.request = request;
		this.response = response;
		this.base = base;
	}

	@Override
	public void setContentType(String contentType) {
		this.response.setContentType(contentType);
	}

	@Override
	public void setOutputStream(InputStream content) throws IOException {
		PrintWriter writer = this.response.getWriter();
		writer.write(IOUtils.toString(content));
		//IOUtils.copy(content, this.response.getOutputStream());
	}

	@Override
	public void setHandled() {
		this.base.setHandled(true);
	}

	@Override
	public InputStream getAutocomplete(String path) throws Exception {
		return new ClientCommunication("http://localhost").httpGet(path + queryString()).getEntity().getContent();
	}

	private String queryString() {
		ArrayList<String> parameters = new ArrayList<String>();
		parameters.add(queryParameter("q", "URI", request.getParameter("q")));
		parameters.add(queryParameter("fq", "dataset", request.getParameter("dataset")));
		parameters.add(queryParameter("fq", "type", "concept"));
		parameters.add(solrExtraParameter("wt", "json"));
		parameters.add(solrExtraParameter("fl", "URI"));
		parameters.add(solrExtraParameter("indent", "true"));
		return "?" + StringUtils.join(parameters, "&");
	}

	private String solrExtraParameter(String name, String value) {
		return name + "=" + value;
	}

	private String queryParameter(String queryParameter, String solrParameter, String solrValue) {
		return queryParameter + "=" + solrParameter + ":" + solrValue;
	}

}
