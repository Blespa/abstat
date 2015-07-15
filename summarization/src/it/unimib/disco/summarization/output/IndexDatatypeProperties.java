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
		String dataset = args[3];
		
		/*Step: Datatype-properties import.*/
		
		String serverUrl = "http://"+host+":"+port+"/solr/indexing";
		HttpSolrServer client = new HttpSolrServer(serverUrl);
		
		datatypePropertiesImport(client,pathFile,dataset);
	}
	
	private static void datatypePropertiesImport (HttpSolrServer client, String pathFile, String dataset) throws FileNotFoundException, IOException, SolrServerException
	{
		ArrayList <String> datatypeProperties = takeOnlyDatatypeProperties(pathFile);
		ArrayList <String> subtypeOfDatatypeProperties = takeOnlySubtypeOfDatatypeProperties(pathFile);
		
		indexDatatypeProperties(client,datatypeProperties,subtypeOfDatatypeProperties,dataset);
	}
	
	private static void indexDatatypeProperties(HttpSolrServer client, ArrayList<String> datatypeProperties, ArrayList<String> subtypeOfDatatypeProperties, String dataset) throws IOException, SolrServerException
	{
		int numberOfDatatypeProperties = datatypeProperties.size();
		
		for (int i = 0; i < numberOfDatatypeProperties; i++)
		{
			String datatypeProperty = datatypeProperties.get(i);
			String subtypeOfDatatypeProperty = subtypeOfDatatypeProperties.get(i);
			
			SolrInputDocument doc = new SolrInputDocument();
			doc.setField("idDocument", (i+1+20));
			doc.setField("datatypeProperty", datatypeProperty);
			doc.setField("type", "datatypeProperty");
			doc.setField("dataset", dataset);
			doc.setField("subtype", subtypeOfDatatypeProperty);
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
	
	private static ArrayList <String> takeOnlySubtypeOfDatatypeProperties (String pathFile) throws FileNotFoundException, IOException
	{
		String path = pathFile;
		BufferedReader reader = new BufferedReader(new FileReader(path));
		
		ArrayList <String> subtypeOfDatatypeProperties = new ArrayList <String> ();
		String subtypeOfDatatypeProperty = "";
		int contatore = 0;
    	
    	String lineRead = reader.readLine();
    	
    	while ((lineRead != null) && (contatore < lineRead.length()))
		{
			for (int i = 0; i < lineRead.length(); i++)
			{
				if (lineRead.charAt(i) != '#')
				{
					//System.out.println("sono dentro il primo if");
					subtypeOfDatatypeProperty += lineRead.charAt(i);
				}
				else
				{
					if (lineRead.charAt(i) == '#')
					{
						//System.out.println("sono dentro il secondo if");
						subtypeOfDatatypeProperty = "";
					}
				}
				contatore++;
			}
			
			contatore = 0;
			
			subtypeOfDatatypeProperties.add(subtypeOfDatatypeProperty);
			
			lineRead = reader.readLine();
		}
    	
    	reader.close();
    	
		return subtypeOfDatatypeProperties;
	}
}