package it.unimib.disco.summarization.output;

/*import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrInputDocument;

@SuppressWarnings("deprecation")*/
public class IndexConcepts
{
	public static void main(String[] args) //throws SolrServerException, IOException
	{
		/*Step: Import dei concetti in Solr.*/
		
		/*String serverUrl = "http://localhost:8886/solr/coreSolr"; //URL where is up Solr server
		HttpSolrServer solr = new HttpSolrServer(serverUrl); //connect to Solr server
		solr.deleteByQuery("*:*"); //delete all documents into Solr server at the start
		
		conceptsImport(solr);*/
	}
	
	/*private static void conceptsImport (HttpSolrServer solr) throws FileNotFoundException, IOException, SolrServerException
	{
		ArrayList <String> concepts = takeOnlyConcepts();
		
		indexDocuments(solr, concepts);	
	}
	
	private static ArrayList <String> takeOnlyConcepts () throws FileNotFoundException, IOException
	{
		String pathFile = "/home/edoardo/workspace/Issue#2/countConcepts.txt";
		BufferedReader reader = new BufferedReader(new FileReader(pathFile));
		
		int numberOfConcepts = 0;
		ArrayList <String> concepts = new ArrayList <String> ();
		
    	boolean trovatoDoppioCancelletto = false;
    	String concept = "";
    	
    	String line = reader.readLine();
		numberOfConcepts = (numberOfConcepts + 1);
    	
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
    	
    	numberOfConcepts = (numberOfConcepts - 1);
    	reader.close();
    	
		return concepts;
	}

	private static void indexDocuments (HttpSolrServer solr, ArrayList <String> concepts) throws SolrServerException, IOException
	{
		int numberOfConcepts = concepts.size();
		
		for (int i = 0; i < numberOfConcepts; i++)
		{
			String concept = concepts.get(i);
			SolrInputDocument doc = new SolrInputDocument ();
			doc.setField("idDocument", i+1);
			doc.setField("conceptName", concept);
			solr.add(doc);
		}
		
		solr.commit(true, true);
	}*/
}
