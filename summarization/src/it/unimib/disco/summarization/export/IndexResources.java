package it.unimib.disco.summarization.export;

import it.unimib.disco.summarization.dataset.InputFile;
import it.unimib.disco.summarization.ontology.RDFResource;
import it.unimib.disco.summarization.ontology.TypeOf;
import it.unimib.disco.summarization.web.SolrConnector;

import org.apache.solr.client.solrj.impl.HttpSolrServer;
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
		while(input.hasNextLine()){
			String[] line = input.nextLine().split("##");
			String resource = line[0];
			String localName = new RDFResource(resource).localName();
			Long occurrences = Long.parseLong(line[1]);
			String subtype = new TypeOf(domain).resource(resource);
			
			SolrInputDocument doc = new SolrInputDocument();
			doc.setField("URI", resource);
			doc.setField("type", type);
			doc.setField("dataset", dataset);
			doc.setField("subtype", subtype);
			doc.setField("fullTextSearchField", localName);
			doc.setField("occurrence", occurrences);
			client.add(doc);
		}
		
		client.commit(true, true);
	}
}