package it.unimib.disco.summarization.export;

import it.unimib.disco.summarization.dataset.FileSystemConnector;
import it.unimib.disco.summarization.dataset.TextInput;
import it.unimib.disco.summarization.ontology.RDFResource;
import it.unimib.disco.summarization.ontology.TypeOf;

import java.io.File;

import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrInputDocument;

public class IndexAKP
{
	public static void main(String[] args) throws Exception
	{
		Events.summarization();
		
		try{
			String host = args[0];
			String port = args[1];
			String pathFile = args[2];
			String dataset = args[3];
			String type = args[4];
			String domain = args[5];
			
			String serverUrl = "http://"+host+":"+port+"/solr/indexing";
			HttpSolrServer client = new HttpSolrServer(serverUrl);
			TypeOf typeOf = new TypeOf(domain);
			
			TextInput input = new TextInput(new FileSystemConnector(new File(pathFile)));
			
			while(input.hasNextLine()){
				String[] line = input.nextLine().split("##");
				String subject = line[0];
				String subjectLocalName = new RDFResource(subject).localName();
				String property = line[1];
				String propertyLocalName = new RDFResource(property).localName();
				String object = line[2];
				String objectLocalName = new RDFResource(object).localName();
				Long occurrences = Long.parseLong(line[3]);
				String subtype = typeOf(subject, object, typeOf, type);
				
				SolrInputDocument doc = new SolrInputDocument();
				doc.setField("URI", new String[]{
						subject, property, object
				});
				doc.setField("type", type);
				doc.setField("dataset", dataset);
				doc.setField("subtype", subtype);
				doc.setField("fullTextSearchField", new String[]{
						subjectLocalName, propertyLocalName, objectLocalName
				});
				doc.setField("occurrence", occurrences);
				client.add(doc);
			}
			
			client.commit(true, true);
		}
		catch(Exception e){
			Events.summarization().error("", e);
		}
	}
	
	private static String typeOf(String subject, String object, TypeOf typeof, String type) {
		if(type.equals("datatype")) return typeof.datatypeAKP(subject);
		return typeof.objectAKP(subject, object);
	}
}