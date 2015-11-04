package it.unimib.disco.summarization.export;

import it.unimib.disco.summarization.dataset.FileSystemConnector;
import it.unimib.disco.summarization.dataset.TextInput;
import it.unimib.disco.summarization.ontology.RDFResource;
import it.unimib.disco.summarization.ontology.TypeOf;

import java.io.File;

import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrInputDocument;

public class IndexResources
{
	public static void main (String[] args) throws Exception
	{
		Events.summarization();
		
		try{
			String host = args[0];
			String port = args[1];
			String file = args[2];
			String dataset = args[3];
			String type = args[4];
			String domain = args[5];
			
			String serverUrl = "http://"+host+":"+port+"/solr/indexing";
			HttpSolrServer client = new HttpSolrServer(serverUrl);
			TypeOf typeOf = new TypeOf(domain);
			
			TextInput input = new TextInput(new FileSystemConnector(new File(file)));
			
			while(input.hasNextLine()){
				String[] line = input.nextLine().split("##");
				String resource = line[0];
				String localName = new RDFResource(resource).localName();
				Long occurrences = Long.parseLong(line[1]);
				String subtype = typeOf.resource(resource);
				
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
		catch(Exception e){
			Events.summarization().error("", e);
		}
	}
}
