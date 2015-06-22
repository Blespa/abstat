package it.unimib.disco.summarization.output;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrInputDocument;

public class IndexDatatypeProperties
{
	public static void main (String[] args) throws SolrServerException, IOException
	{
		/*Receive three arguments from script (that are 'host', 'port' and 'pathFile').*/
		
		String host = args[0];
		String port = args[1];
		String pathFile = args[2];
		
		/*Step: Datatype-properties import.*/
		
		String serverUrl = "http://"+host+":"+port+"/solr/indexing";
		HttpSolrServer client = new HttpSolrServer(serverUrl);
		
		datatypePropertiesImport(client,pathFile);
	}
	
	private static void datatypePropertiesImport (HttpSolrServer client, String pathFile) throws FileNotFoundException, IOException, SolrServerException
	{
		ArrayList <String> datatypeProperties = takeOnlyDatatypeProperties(pathFile);
		
		indexDatatypeProperties(client,datatypeProperties);
	}
	
	private static void indexDatatypeProperties(HttpSolrServer client, ArrayList<String> datatypeProperties) throws IOException, SolrServerException
	{
		int numberOfDatatypeProperties = datatypeProperties.size();
		
		for (int i = 0; i < numberOfDatatypeProperties; i++)
		{
			String datatypeProperty = datatypeProperties.get(i);
			SolrInputDocument doc = new SolrInputDocument();
			doc.setField("idDocument", (i+1+20));
			doc.setField("datatypeProperty", datatypeProperty);
			doc.setField("type", "datatypeProperty");
			client.add(doc);
		}
		
		client.commit(true,true);
	}

	private static ArrayList<String> takeOnlyDatatypeProperties (String pathFile) throws FileNotFoundException, IOException
	{
		String path = pathFile;
		BufferedReader reader = new BufferedReader(new FileReader(path));
		
		int numberOfDatatypeProperties = 0;
		ArrayList <String> datatypeProperties = new ArrayList <String> ();
		
    	boolean trovatoDoppioCancelletto = false;
    	String datatypeProperty = "";
    	
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
    					datatypeProperty += line.charAt(i);
    				}
    				else
    				{
    					if ((line.charAt(i) != '#') && (line.charAt(i+1) == '#'))
    					{
    						datatypeProperty += line.charAt(i);
    					}
    					else
    					{
    						if ((line.charAt(i) == '#') && (line.charAt(i+1) != '#'))
    						{
    							datatypeProperty += line.charAt(i);
    						}
    					}
    				}
    			}
    		}
    		
    		if (!(datatypeProperty.equalsIgnoreCase("")))
    		{
    			datatypeProperties.add(datatypeProperty);
    		}
    		
    		datatypeProperty = "";
    		trovatoDoppioCancelletto = false;
    		line = reader.readLine();
    		numberOfDatatypeProperties = (numberOfDatatypeProperties + 1);
    		
    	}
    	
    	reader.close();
    	
		return datatypeProperties;
	}
}