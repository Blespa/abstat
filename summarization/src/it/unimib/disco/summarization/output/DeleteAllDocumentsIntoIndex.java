package it.unimib.disco.summarization.output;

import java.io.IOException;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;

public class DeleteAllDocumentsIntoIndex
{
	public static void main (String[] args) throws SolrServerException, IOException
	{
		/*Receive two arguments from script (that are 'host' and 'port').*/
		
		String host = args[0];
		String port = args[1];
		String datasetInput = args[2];
		
		/*Delete all the documents stored in the index.*/

		String serverUrl = "http://"+host+":"+port+"/solr/indexing";
		HttpSolrServer client = new HttpSolrServer(serverUrl);
		
		client.deleteByQuery("dataset:" + datasetInput);
		
		client.commit();
	}
}
