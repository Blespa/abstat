package it.unimib.disco.summarization.export;

import it.unimib.disco.summarization.ontology.RDFResource;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
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
			String pathFile = args[2];
			String dataset = args[3];
			String type = args[4];
			
			String serverUrl = "http://"+host+":"+port+"/solr/indexing";
			HttpSolrServer client = new HttpSolrServer(serverUrl);
			
			conceptsImport(client, pathFile, dataset, type);
		}
		catch(Exception e){
			Events.summarization().error("", e);
		}
	}
	
	private static void conceptsImport (HttpSolrServer client, String pathFile, String dataset, String type) throws Exception
	{
		ArrayList<String> concepts = takeOnlyConcepts(pathFile);
		ArrayList<String> subtypeOfConcepts = takeOnlySubtypeOfConcepts(pathFile);
		ArrayList<String> localNamesOfConcepts = takeOnlyLocalNamesOfConcepts(concepts);
		ArrayList<Long> occurrences = selectOccurrences(pathFile);
		
		indexDocuments(client,concepts,subtypeOfConcepts,localNamesOfConcepts,dataset, occurrences, type);
	}
	
	private static ArrayList<Long> selectOccurrences(String pathFile) throws Exception {
		ArrayList<Long> result = new ArrayList<Long>();
		LineIterator lines = FileUtils.lineIterator(new File(pathFile));
		while(lines.hasNext()){
			String line = lines.next();
			result.add(Long.parseLong(line.split("##")[1]));
		}
		return result;
	}

	private static void indexDocuments(HttpSolrServer client, ArrayList<String> concepts, ArrayList <String> subtypeOfConcepts, ArrayList <String> localNamesOfConcepts, String dataset, ArrayList<Long> occurrences, String type) throws Exception
	{
		int numberOfConcepts = concepts.size();
		
		for (int i = 0; i < numberOfConcepts; i++)
		{
			String concept = concepts.get(i);
			String subtypeOfConcept = subtypeOfConcepts.get(i);
			String localNameOfConcept = localNamesOfConcepts.get(i);
			Long occurrence = occurrences.get(i);
			
			SolrInputDocument doc = new SolrInputDocument();
			doc.setField("URI", concept);
			doc.setField("type", type);
			doc.setField("dataset", dataset);
			doc.setField("subtype", subtypeOfConcept);
			doc.setField("fullTextSearchField", localNameOfConcept);
			doc.setField("occurrence", occurrence);
			client.add(doc);
		}
		
		client.commit(true, true);
	}

	private static ArrayList<String> takeOnlyConcepts(String pathFile) throws Exception
	{
		ArrayList <String> concepts = new ArrayList<String>();
    	
		BufferedReader reader = new BufferedReader(new FileReader(pathFile));
    	String line = reader.readLine();
    	while (line != null)
    	{
    		concepts.add(line.split("##")[0]);
    		line = reader.readLine();
    	}
    	reader.close();
    	
		return concepts;
	}
	
	private static ArrayList <String> takeOnlySubtypeOfConcepts(String pathFile) throws Exception
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
					subtypeOfConcept += lineRead.charAt(i);
				}
				else
				{
					if (lineRead.charAt(i) == '#')
					{
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
	
	private static ArrayList <String> takeOnlyLocalNamesOfConcepts (ArrayList <String> concepts)
	{
		ArrayList <String> localNamesOfConcepts = new ArrayList <String> ();
		
		for (int i = 0; i < concepts.size(); i++) 
		{
			localNamesOfConcepts.add(new RDFResource(concepts.get(i)).localName());
		}
		
		return localNamesOfConcepts;
	}
}
