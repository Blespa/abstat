package it.unimib.disco.summarization.output;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.common.SolrInputDocument;

public class IndexProperties
{
	public static void main (String[] args) throws SolrServerException, IOException
	{
		/*Receive three arguments from script (that are 'host', 'port' and 'pathFile').*/
		
		String host = args[0];
		String port = args[1];
		String pathFile = args[2];
		
		/*Step: Properties import.*/
		
		String serverUrl = "http://"+host+":"+port+"/solr/indexing";
		HttpSolrClient client = new HttpSolrClient(serverUrl);
		client.deleteByQuery("*:*");
		
		propertiesImport(client, pathFile);
	}
	
	private static void propertiesImport (HttpSolrClient client, String pathFile) throws FileNotFoundException, IOException, SolrServerException
	{
		ArrayList <String> properties = takeOnlyProperties(pathFile);
		
		indexProperties(client, properties);
	}
	
	private static void indexProperties(HttpSolrClient client, ArrayList<String> properties) throws IOException, SolrServerException
	{
		int numberOfProperties = properties.size();
		
		for (int i = 0; i < numberOfProperties; i++)
		{
			String property = properties.get(i);
			SolrInputDocument doc = new SolrInputDocument();
			doc.setField("idDocument", i+1);
			doc.setField("property", property);
			client.add(doc);
		}
		
		client.commit(true, true);
	}

	private static ArrayList<String> takeOnlyProperties (String pathFile) throws FileNotFoundException, IOException
	{
		String path = pathFile;
		BufferedReader reader = new BufferedReader(new FileReader(path));
		
		int numberOfProperties = 0;
		ArrayList <String> properties = new ArrayList <String> ();
		
    	boolean trovatoDoppioCancelletto = false;
    	String property = "";
    	
    	String line = reader.readLine();
		numberOfProperties = (numberOfProperties + 1);
    	
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
    					property += line.charAt(i);
    				}
    				else
    				{
    					if ((line.charAt(i) != '#') && (line.charAt(i+1) == '#'))
    					{
    						property += line.charAt(i);
    					}
    					else
    					{
    						if ((line.charAt(i) == '#') && (line.charAt(i+1) != '#'))
    						{
    							property += line.charAt(i);
    						}
    					}
    				}
    			}
    		}
    		
    		if (!(property.equalsIgnoreCase("")) && (!(property.equalsIgnoreCase("Property"))))
    		{
    				properties.add(property);
    		}
    		
    		property = "";
    		trovatoDoppioCancelletto = false;
    		line = reader.readLine();
    		numberOfProperties = (numberOfProperties + 1);
    		
    	}
    	
    	numberOfProperties = (numberOfProperties - 1);
    	reader.close();
    	
		return properties;
	}
}
