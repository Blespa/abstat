package it.unimib.disco.summarization.output;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrInputDocument;

public class IndexConcepts
{
	public static void main (String[] args) throws SolrServerException, IOException
	{
		/*Receive four arguments from script (that are 'host', 'port' and 'pathFile').*/
		
		String host = args[0];
		String port = args[1];
		String pathFile = args[2];
		String dataset = args[3];
		
		/*Step: Concepts import.*/
		
		String serverUrl = "http://"+host+":"+port+"/solr/indexing";
		HttpSolrServer client = new HttpSolrServer(serverUrl);
		
		//client.deleteByQuery("*:*");
		conceptsImport(client,pathFile,dataset);
	}
	
	private static void conceptsImport (HttpSolrServer client, String pathFile, String dataset) throws FileNotFoundException, IOException, SolrServerException
	{
		ArrayList <String> concepts = takeOnlyConcepts(pathFile);
		ArrayList <String> subtypeOfConcepts = takeOnlySubtypeOfConcepts(pathFile);
		
		indexDocuments(client,concepts,subtypeOfConcepts,dataset);
	}
	
	private static void indexDocuments(HttpSolrServer client, ArrayList<String> concepts, ArrayList <String> subtypeOfConcepts, String dataset) throws IOException, SolrServerException
	{
		int numberOfConcepts = concepts.size();
		
		for (int i = 0; i < numberOfConcepts; i++)
		{
			String concept = concepts.get(i);
			String subtypeOfConcept = subtypeOfConcepts.get(i);
			
			SolrInputDocument doc = new SolrInputDocument();
			doc.setField("idDocument", i+1);
			doc.setField("concept", concept);
			doc.setField("type", "concept");
			doc.setField("dataset", dataset);
			doc.setField("subtype", subtypeOfConcept);
			client.add(doc);
		}
		
		client.commit(true,true);
	}

	private static ArrayList<String> takeOnlyConcepts(String pathFile) throws FileNotFoundException, IOException
	{
		String path = pathFile;
		BufferedReader reader = new BufferedReader(new FileReader(path));
		
		int numberOfConcepts = 0;
		ArrayList <String> concepts = new ArrayList <String> ();
		
    	boolean trovatoDoppioCancelletto = false;
    	String concept = "";
    	
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
    					concept += line.charAt(i);
    				}
    				else
    				{
    					if ((line.charAt(i) != '#') && (line.charAt(i+1) == '#'))
    					{
    						concept += line.charAt(i);
    					}
    					else
    					{
    						if ((line.charAt(i) == '#') && (line.charAt(i+1) != '#'))
    						{
    							concept += line.charAt(i);
    						}
    					}
    				}
    			}
    		}
    		
    		if (!(concept.equalsIgnoreCase("")) && (!(concept.equalsIgnoreCase("Concept"))))
    		{
    				concepts.add(concept);
    		}
    		
    		concept = "";
    		trovatoDoppioCancelletto = false;
    		line = reader.readLine();
    		numberOfConcepts = (numberOfConcepts + 1);
    		
    	}
    	
    	reader.close();
    	
		return concepts;
	}
	
	private static ArrayList <String> takeOnlySubtypeOfConcepts(String pathFile) throws FileNotFoundException, IOException
	{
		String path = pathFile;
		BufferedReader reader = new BufferedReader(new FileReader(path));
		
		ArrayList <String> subtypeOfConcepts = new ArrayList <String> ();
		String subtypeOfConcept = "";
		int contatore = 0;
    	
    	String lineRead = reader.readLine();
    	
    	while ((lineRead != null) && (contatore < lineRead.length()))
		{
			for (int i = 0; i < lineRead.length(); i++)
			{
				if (lineRead.charAt(i) != '#')
				{
					//System.out.println("sono dentro il primo if");
					subtypeOfConcept += lineRead.charAt(i);
				}
				else
				{
					if (lineRead.charAt(i) == '#')
					{
						//System.out.println("sono dentro il secondo if");
						subtypeOfConcept = "";
					}
				}
				contatore++;
			}
			
			contatore = 0;
			
			subtypeOfConcepts.add(subtypeOfConcept);
			
			lineRead = reader.readLine();
		}
    	
    	reader.close();
    	
		return subtypeOfConcepts;
	}
}
