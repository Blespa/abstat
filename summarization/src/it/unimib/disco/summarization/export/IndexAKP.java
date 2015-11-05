package it.unimib.disco.summarization.export;

import it.unimib.disco.summarization.dataset.InputFile;
import it.unimib.disco.summarization.ontology.RDFResource;
import it.unimib.disco.summarization.ontology.TypeOf;
import it.unimib.disco.summarization.web.SolrConnector;

import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.request.UpdateRequest;
import org.apache.solr.common.SolrInputDocument;

public class IndexAKP{
	
	private HttpSolrServer client;
	private String domain;
	private String type;
	private String dataset;

	public IndexAKP(SolrConnector connector, String dataset, String type, String domain){
		
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
			String subject = line[0];
			String subjectLocalName = new RDFResource(subject).localName();
			String property = line[1];
			String propertyLocalName = new RDFResource(property).localName();
			String object = line[2];
			String objectLocalName = new RDFResource(object).localName();
			Long occurrences = Long.parseLong(line[3]);
			String subtype = typeOf(subject, object);
			
			SolrInputDocument document = new SolrInputDocument();
			document.setField("URI", new String[]{
					subject, property, object
			});
			document.setField("type", type);
			document.setField("dataset", dataset);
			document.setField("subtype", subtype);
			document.setField("fullTextSearchField", new String[]{
					subjectLocalName, propertyLocalName, objectLocalName
			});
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
	}
	
	public void endProcessing() throws Exception{
		client.commit(true, true);
	}
	
	private String typeOf(String subject, String object) {
		TypeOf typeOf = new TypeOf(domain);
		if(type.equals("datatypeAkp")) return typeOf.datatypeAKP(subject);
		return typeOf.objectAKP(subject, object);
	}
}