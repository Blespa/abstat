package it.unimib.disco.summarization.output;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrInputDocument;

public class IndexObjectAkps
{
	public static void main(String[] args) throws SolrServerException, IOException
	{
		/*Receive three arguments from script (that are 'host', 'port' and 'pathFile').*/
		
		String host = args[0];
		String port = args[1];
		String pathFile = args[2];
		String dataset = args[3];
		
		/*Step: Object-akp import.*/
		
		String serverUrl = "http://"+host+":"+port+"/solr/indexing";
		HttpSolrServer client = new HttpSolrServer(serverUrl);
		
		objectAkpsImport(client,pathFile,dataset);
	}
	
	private static void objectAkpsImport (HttpSolrServer client, String pathFile, String dataset) throws SolrServerException, IOException
	{
		ArrayList <String> objectAkps = takeOnlyObjectAkps(pathFile);
		ArrayList <String> subtypeOfObjectAkps = takeOnlySubtypeOfObjectAkps(pathFile);
		
		indexObjectAkps(client,objectAkps,subtypeOfObjectAkps,dataset);
	}

	private static void indexObjectAkps (HttpSolrServer client, ArrayList <String> objectAkps, ArrayList <String> subtypeOfObjectAkps, String dataset) throws SolrServerException, IOException
	{
		int numberOfObjectAkps = objectAkps.size();
		
		for (int i = 0; i < numberOfObjectAkps; i++)
		{
			String objectAkp = objectAkps.get(i);
			String subtypeOfObjectAkp = subtypeOfObjectAkps.get(i);
			
			SolrInputDocument doc = new SolrInputDocument();
			doc.setField("idDocument", (i+1+20+11+5+68));
			doc.setField("objectAkp", objectAkp);
			doc.setField("type", "objectAkp");
			doc.setField("dataset", dataset);
			doc.setField("subtype", subtypeOfObjectAkp);
			client.add(doc);
		}
		
		client.commit(true,true);
	}
	
	private static ArrayList <String> takeOnlyObjectAkps (String pathFile) throws IOException
	{	
		String akpSubject = "";
		String akpProperty = "";
		String akpObject = "";
		boolean trovatoPrimoDoppioCancelletto = false;
		boolean trovatoSecondoDoppioCancelletto = false;
		boolean trovatoTerzoDoppioCancelletto = false;
		
		ArrayList <String> subjectsAkps = new ArrayList <String> ();
		ArrayList <String> propertiesAkps = new ArrayList <String> ();
		ArrayList <String> objectsAkps = new ArrayList <String> (); 
		
		String path = pathFile;
		BufferedReader reader = new BufferedReader(new FileReader(path));
		
		String line = reader.readLine();
		
		while (line != null)
		{
			for (int i = 0; i < line.length() && trovatoTerzoDoppioCancelletto == false; i++)
			{
				if (trovatoPrimoDoppioCancelletto == false) 
				{
					if ((line.charAt(i) != '#') && (line.charAt(i+1) != '#'))
					{
						akpSubject += line.charAt(i);
					}
					if ((line.charAt(i) != '#') && (line.charAt(i+1) == '#'))
					{
						akpSubject += line.charAt(i);
					}
					if ((line.charAt(i) == '#') && (line.charAt(i+1) != '#'))
					{
						akpSubject += line.charAt(i);
					}
					if ((line.charAt(i) == '#') && (line.charAt(i+1) == '#'))
					{
						trovatoPrimoDoppioCancelletto = true;
					}
				}
				
				if (trovatoPrimoDoppioCancelletto == true)
				{
					if (trovatoSecondoDoppioCancelletto == false)
					{
						if ((line.charAt(i+2) != '#') && (line.charAt(i+3) != '#'))
						{
							akpProperty += line.charAt(i+2);
						}
						if ((line.charAt(i+2) != '#') && (line.charAt(i+3) == '#'))
						{
							akpProperty += line.charAt(i+2);
						}
						if ((line.charAt(i+2) == '#') && (line.charAt(i+3) != '#'))
						{
							akpProperty += line.charAt(i+2);
						}
						if ((line.charAt(i+2) == '#') && (line.charAt(i+3) == '#'))
						{
							trovatoSecondoDoppioCancelletto = true;
						}
					}
					if (trovatoSecondoDoppioCancelletto == true)
					{
						if ((line.charAt(i+4) != '#') && (line.charAt(i+5) != '#'))
						{
							akpObject += line.charAt(i+4);
						}
						if ((line.charAt(i+4) != '#') && (line.charAt(i+5) == '#'))
						{
							akpObject += line.charAt(i+4);
						}
						if ((line.charAt(i+4) == '#') && (line.charAt(i+5) != '#'))
						{
							akpObject += line.charAt(i+4);
						}
						if ((line.charAt(i+4) == '#') && (line.charAt(i+5) == '#'))
						{
							trovatoTerzoDoppioCancelletto = true;
						}
					}
				}
			}
			
			if (!(akpSubject.equalsIgnoreCase("")))
    		{
    				subjectsAkps.add(akpSubject);
    		}
			if (!(akpProperty.equalsIgnoreCase("")))
    		{
    				propertiesAkps.add(akpProperty);
    		}
			if (!(akpObject.equalsIgnoreCase("")))
    		{
    				objectsAkps.add(akpObject);
    		}
			
			akpSubject = "";
			akpProperty = "";
			akpObject = "";
			trovatoPrimoDoppioCancelletto = false;
			trovatoSecondoDoppioCancelletto = false;
			trovatoTerzoDoppioCancelletto = false;
			
			line = reader.readLine();
		}
		
		reader.close();
		
		ArrayList <String> akps = new ArrayList <String> ();
		
		for (int i = 0; i < subjectsAkps.size(); i++) //tanto hanno tutti la stessa lunghezza
		{
			String akp = subjectsAkps.get(i)+","+propertiesAkps.get(i)+","+objectsAkps.get(i);
			akps.add(akp);
		}
		
		return akps;
	}
	
	private static ArrayList <String> takeOnlySubtypeOfObjectAkps(String pathFile) throws FileNotFoundException, IOException
	{
		String path = pathFile;
		BufferedReader reader = new BufferedReader(new FileReader(path));
		
		ArrayList <String> subtypeOfObjectAkps = new ArrayList <String> ();
		String subtypeOfObjectAkp = "";
		int contatore = 0;
    	
    	String lineRead = reader.readLine();
    	
    	while ((lineRead != null) && (contatore < lineRead.length()))
		{
			for (int i = 0; i < lineRead.length(); i++)
			{
				if (lineRead.charAt(i) != '#')
				{
					//System.out.println("sono dentro il primo if");
					subtypeOfObjectAkp += lineRead.charAt(i);
				}
				else
				{
					if (lineRead.charAt(i) == '#')
					{
						//System.out.println("sono dentro il secondo if");
						subtypeOfObjectAkp = "";
					}
				}
				contatore++;
			}
			
			contatore = 0;
			
			subtypeOfObjectAkps.add(subtypeOfObjectAkp);
			
			lineRead = reader.readLine();
		}
    	
    	reader.close();
    	
		return subtypeOfObjectAkps;
	}
}