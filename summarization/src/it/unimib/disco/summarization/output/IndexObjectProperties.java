package it.unimib.disco.summarization.output;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrInputDocument;

public class IndexObjectProperties
{
	public static void main (String[] args) throws SolrServerException, IOException
	{
		/*Receive three arguments from script (that are 'host', 'port' and 'pathFile').*/
		
		String host = args[0];
		String port = args[1];
		String pathFile = args[2];
		String dataset = args[3];
		
		/*Step: Object-properties import.*/
		
		String serverUrl = "http://"+host+":"+port+"/solr/indexing";
		HttpSolrServer client = new HttpSolrServer(serverUrl);
		
		objectPropertiesImport(client,pathFile,dataset);
	}
	
	private static void objectPropertiesImport (HttpSolrServer client, String pathFile, String dataset) throws FileNotFoundException, IOException, SolrServerException
	{
		ArrayList <String> objectProperties = takeOnlyObjectProperties(pathFile);
		
		indexObjectProperties(client,objectProperties,dataset);
	}
	
	private static void indexObjectProperties(HttpSolrServer client, ArrayList<String> objectProperties, String dataset) throws IOException, SolrServerException
	{
		int numberOfObjectProperties = objectProperties.size();
		
		for (int i = 0; i < numberOfObjectProperties; i++)
		{
			String objectProperty = objectProperties.get(i);
			SolrInputDocument doc = new SolrInputDocument();
			doc.setField("idDocument", (i+1+20+11));
			doc.setField("objectProperty", objectProperty);
			doc.setField("type", "objectProperty");
			doc.setField("dataset", dataset);
			client.add(doc);
		}
		
		client.commit(true,true);
	}

	private static ArrayList<String> takeOnlyObjectProperties (String pathFile) throws FileNotFoundException, IOException
	{
		String path = pathFile;
		BufferedReader reader = new BufferedReader(new FileReader(path));
		
		int numberOfObjectProperties = 0;
		ArrayList <String> objectProperties = new ArrayList <String> ();
		
    	boolean trovatoDoppioCancelletto = false;
    	String objectProperty = "";
    	
    	String line = reader.readLine();
    	
    	while (line != null)
    	{
    		for (int i = 0; i < line.length() && trovatoDoppioCancelletto == false; i++)
    		{
    			if ((line.charAt(i) == '#') && (line.charAt(i+1) == '#'))
    			{
    				trovatoDoppioCancelletto = true;
    			}
    			else
    			{
    				if ((line.charAt(i) != '#') && (line.charAt(i+1) != '#'))
    				{
    					objectProperty += line.charAt(i);
    				}
    				else
    				{
    					if ((line.charAt(i) != '#') && (line.charAt(i+1) == '#'))
    					{
    						objectProperty += line.charAt(i);
    					}
    					else
    					{
    						if ((line.charAt(i) == '#') && (line.charAt(i+1) != '#'))
    						{
    							objectProperty += line.charAt(i);
    						}
    					}
    				}
    			}
    		}
    		
    		if (!(objectProperty.equalsIgnoreCase("")))
    		{
    				objectProperties.add(objectProperty);
    		}
    		
    		objectProperty = "";
    		trovatoDoppioCancelletto = false;
    		line = reader.readLine();
    		numberOfObjectProperties = (numberOfObjectProperties + 1);
    		
    	}
    	
    	reader.close();
    	
		return objectProperties;
	}
}