package it.unimib.disco.summarization.export;

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
		String host = args[0];
		String port = args[1];
		String pathFile = args[2];
		String dataset = args[3];
		
		String serverUrl = "http://"+host+":"+port+"/solr/indexing";
		HttpSolrServer client = new HttpSolrServer(serverUrl);
		
		objectPropertiesImport(client,pathFile,dataset);
	}
	
	private static void objectPropertiesImport (HttpSolrServer client, String pathFile, String dataset) throws FileNotFoundException, IOException, SolrServerException
	{
		ArrayList <String> objectProperties = takeOnlyObjectProperties(pathFile);
		ArrayList <String> subtypeOfObjectProperties = takeOnlySubtypeOfObjectProperties(pathFile);
		ArrayList <String> localNamesOfObjectProperties = takeOnlyLocalNamesOfObjectProperties(objectProperties);
		
		indexObjectProperties(client,objectProperties,subtypeOfObjectProperties,localNamesOfObjectProperties,dataset);
	}
	
	private static void indexObjectProperties(HttpSolrServer client, ArrayList<String> objectProperties, ArrayList <String> subtypeOfObjectProperties, ArrayList <String> localNamesOfObjectProperties, String dataset) throws IOException, SolrServerException
	{
		int numberOfObjectProperties = objectProperties.size();
		
		for (int i = 0; i < numberOfObjectProperties; i++)
		{
			String objectProperty = objectProperties.get(i);
			String subtypeOfObjectProperty = subtypeOfObjectProperties.get(i);
			String localNameOfObjectProperty = localNamesOfObjectProperties.get(i);
			
			SolrInputDocument doc = new SolrInputDocument();
			//doc.setField("idDocument", (i+1+20+11));
			doc.setField("URI", objectProperty);
			doc.setField("type", "objectProperty");
			doc.setField("dataset", dataset);
			doc.setField("subtype", subtypeOfObjectProperty);
			doc.setField("fullTextSearchField", localNameOfObjectProperty);
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
    				objectProperty += "";
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
    		
    		//if (!(objectProperty.equalsIgnoreCase("")))
    		//{
    		objectProperties.add(objectProperty);
    		//}
    		
    		objectProperty = "";
    		trovatoDoppioCancelletto = false;
    		line = reader.readLine();
    		numberOfObjectProperties = (numberOfObjectProperties + 1);
    		
    	}
    	
    	reader.close();
    	
		return objectProperties;
	}
	
	private static ArrayList <String> takeOnlySubtypeOfObjectProperties (String pathFile) throws FileNotFoundException, IOException
	{
		String path = pathFile;
		BufferedReader reader = new BufferedReader(new FileReader(path));
		
		ArrayList <String> subtypeOfObjectProperties = new ArrayList <String> ();
		String subtypeOfObjectProperty = "";
		int contatore = 0;
    	
    	String lineRead = reader.readLine();
    	
    	while ((lineRead != null) && (contatore < lineRead.length()))
		{
			for (int i = 0; i < lineRead.length(); i++)
			{
				if (lineRead.charAt(i) != '#')
				{
					subtypeOfObjectProperty += lineRead.charAt(i);
				}
				else
				{
					if (lineRead.charAt(i) == '#')
					{
						subtypeOfObjectProperty = "";
					}
				}
				contatore++;
			}
			
			contatore = 0;
			
			subtypeOfObjectProperties.add(subtypeOfObjectProperty);
			
			lineRead = reader.readLine();
		}
    	
    	reader.close();
    	
		return subtypeOfObjectProperties;
	}
	
	private static ArrayList <String> takeOnlyLocalNamesOfObjectProperties (ArrayList <String> objectProperties)
	{
		String objectProperty = "";
		String localNameOfobjectProperty = "";
		ArrayList <String> localNamesOfobjectProperties = new ArrayList <String> ();
		
		for (int i = 0; i < objectProperties.size(); i++) 
		{
			objectProperty = objectProperties.get(i);
					
			for (int j = 0; j < objectProperty.length(); j++) 
			{
				if (objectProperty.charAt(j) != '/')
				{
					localNameOfobjectProperty += objectProperty.charAt(j);
				}
				else
				{
					if (objectProperty.charAt(j) == '/')
					{
						localNameOfobjectProperty = "";
					}
				}
			}	
			localNamesOfobjectProperties.add(localNameOfobjectProperty);
		}
		
		return localNamesOfobjectProperties;
	}
}