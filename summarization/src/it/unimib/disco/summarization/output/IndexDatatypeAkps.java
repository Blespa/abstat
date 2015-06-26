package it.unimib.disco.summarization.output;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrInputDocument;

public class IndexDatatypeAkps
{
	public static void main(String[] args) throws IOException, SolrServerException
	{
		/*Receive three arguments from script (that are 'host', 'port' and 'pathFile').*/
		
		String host = args[0];
		String port = args[1];
		String pathFile = args[2];
		
		/*Step: Datatype-akp import.*/
		
		String serverUrl = "http://"+host+":"+port+"/solr/indexing";
		HttpSolrServer client = new HttpSolrServer(serverUrl);
		
		datatypeAkpsImport(client,pathFile);
	}

	private static void datatypeAkpsImport (HttpSolrServer client, String pathFile) throws IOException, SolrServerException
	{
		ArrayList <String> datatypeAkps = takeOnlyDatatypeAkps(pathFile);
		indexDatatypeAkps(client,datatypeAkps);
	}

	private static void indexDatatypeAkps (HttpSolrServer client, ArrayList <String> datatypeAkps) throws SolrServerException, IOException
	{
		int numberOfDatatypeAkps = datatypeAkps.size();
		
		for (int i = 0; i < numberOfDatatypeAkps; i++)
		{
			String datatypeAkp = datatypeAkps.get(i);
			SolrInputDocument doc = new SolrInputDocument();
			doc.setField("idDocument", (i+1+20+11+5));
			doc.setField("datatypeAkp", datatypeAkp);
			doc.setField("type", "datatypeAkp");
			client.add(doc);
		}
		
		client.commit(true,true);
	}
	
	private static ArrayList <String> takeOnlyDatatypeAkps (String pathFile) throws IOException
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
		
		for (int i = 0; i < subjectsAkps.size(); i++)
		{
			String akp = subjectsAkps.get(i)+","+propertiesAkps.get(i)+","+objectsAkps.get(i);
			akps.add(akp);
		}
		
		return akps;
	}
}