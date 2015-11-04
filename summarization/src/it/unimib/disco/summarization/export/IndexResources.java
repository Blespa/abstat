package it.unimib.disco.summarization.export;

import it.unimib.disco.summarization.dataset.InputFile;
import it.unimib.disco.summarization.ontology.RDFResource;
import it.unimib.disco.summarization.ontology.TypeOf;
import it.unimib.disco.summarization.web.SolrConnector;

import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.request.UpdateRequest;
import org.apache.solr.common.SolrInputDocument;

public class IndexResources{
	
	private HttpSolrServer client;
	private String domain;
	private String type;
	private String dataset;

	public IndexResources(SolrConnector connector, String dataset, String type, String domain){
		
		this.client = connector.asUpdateClient();
		this.type = type;
		this.dataset = dataset;
		this.domain = domain;
	}
	
	public void process(InputFile input) throws Exception{
		UpdateRequest request = new UpdateRequest();
		int processed = 0;
		
		while(input.hasNextLine()){
			String[] line = input.nextLine().split("##");
			String resource = line[0];
			String localName = new RDFResource(resource).localName();
			Long occurrences = Long.parseLong(line[1]);
			String subtype = new TypeOf(domain).resource(resource);
			
			SolrInputDocument document = new SolrInputDocument();
			document.setField("URI", resource);
			document.setField("type", type);
			document.setField("dataset", dataset);
			document.setField("subtype", subtype);
			document.setField("fullTextSearchField", localName);
			document.setField("occurrence", occurrences);
			
			request.add(document);
			processed++;
			
			if(processed >= 1000){
				request.process(client);
		        request.clear();
		        processed = 0;
			}
		}
		
		request.process(client);
        request.clear();
		client.commit(true, true);
	}
}