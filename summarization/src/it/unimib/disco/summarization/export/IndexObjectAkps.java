package it.unimib.disco.summarization.export;

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
		String host = args[0];
		String port = args[1];
		String pathFile = args[2];
		String dataset = args[3];
		
		String serverUrl = "http://"+host+":"+port+"/solr/indexing";
		HttpSolrServer client = new HttpSolrServer(serverUrl);
		
		objectAkpsImport(client,pathFile,dataset);
	}
	
	private static void objectAkpsImport (HttpSolrServer client, String pathFile, String dataset) throws SolrServerException, IOException
	{
		ArrayList <String> subjectsOfObjectsAkps = takeOnlySubjectsOfObjectAkps(pathFile);
		ArrayList <String> propertiesOfObjectsAkps = takeOnlyPropertiesOfObjectAkps(pathFile);
		ArrayList <String> objectsOfObjectsAkps = takeOnlyObjectsOfObjectAkps(pathFile);
		
		ArrayList <String> localNamesOfSubjectsOfObjectAkps = takeOnlyLocalNamesOfSubjectsOfObjectAkps(subjectsOfObjectsAkps);
		ArrayList <String> localNamesOfPropertiesOfObjectAkps = takeOnlyLocalNamesOfPropertiesOfObjectAkps(propertiesOfObjectsAkps);
		ArrayList <String> localNamesOfObjectsOfObjectAkps = takeOnlyLocalNamesOfObjectsOfObjectAkps(objectsOfObjectsAkps);
		
		ArrayList <String> subtypeOfObjectAkps = takeOnlySubtypeOfObjectAkps(pathFile);
		
		indexObjectAkps(client,subjectsOfObjectsAkps,propertiesOfObjectsAkps,objectsOfObjectsAkps,localNamesOfSubjectsOfObjectAkps,localNamesOfPropertiesOfObjectAkps,localNamesOfObjectsOfObjectAkps,subtypeOfObjectAkps,dataset);
	}

	private static void indexObjectAkps (HttpSolrServer client, ArrayList <String> subjectsOfObjectsAkps, ArrayList <String> propertiesOfObjectsAkps, ArrayList <String> objectsOfObjectsAkps, ArrayList <String> localNamesOfSubjectsOfObjectAkps, ArrayList <String> localNamesOfPropertiesOfObjectAkps, ArrayList <String> localNamesOfObjectsOfObjectAkps, ArrayList <String> subtypeOfObjectAkps, String dataset) throws SolrServerException, IOException
	{
		int numberOfObjectAkps = subjectsOfObjectsAkps.size();
		
		for (int i = 0; i < numberOfObjectAkps; i++)
		{
			String subjectOfObjectAkp = subjectsOfObjectsAkps.get(i);
			String propertyOfObjectAkp = propertiesOfObjectsAkps.get(i);
			String objectOfObjectAkp = objectsOfObjectsAkps.get(i);
			
			String localNameOfSubjectOfObjectAkp = localNamesOfSubjectsOfObjectAkps.get(i);
			String localNameOfPropertyOfObjectAkp = localNamesOfPropertiesOfObjectAkps.get(i);
			String localNameOfObjectOfObjectAkp = localNamesOfObjectsOfObjectAkps.get(i);
			
			String subtypeOfObjectAkp = subtypeOfObjectAkps.get(i);
			
			String[] akp = new String[3];
			akp[0] = subjectOfObjectAkp;
			akp[1] = propertyOfObjectAkp;
			akp[2] = objectOfObjectAkp;
			
			String[] localNameAkp = new String[3];
			localNameAkp[0] = localNameOfSubjectOfObjectAkp;
			localNameAkp[1] = localNameOfPropertyOfObjectAkp;
			localNameAkp[2] = localNameOfObjectOfObjectAkp;
			
			SolrInputDocument doc = new SolrInputDocument();
			//doc.setField("idDocument", (i+1+20+11+5+68));
			doc.setField("URI", akp);
			doc.setField("type", "objectAkp");
			doc.setField("dataset", dataset);
			doc.setField("subtype", subtypeOfObjectAkp);
			doc.setField("fullTextSearchField", localNameAkp);
			doc.setField("occurrence", 0);
			
			client.add(doc);
			
			akp[0] = "";
			akp[1] = "";
			akp[2] = "";
			
			localNameAkp[0] = "";
			localNameAkp[1] = "";
			localNameAkp[2] = "";
		}
		
		client.commit(true,true);
	}
	
	private static ArrayList <String> takeOnlySubjectsOfObjectAkps (String pathFile) throws IOException
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
						akpSubject += "";
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
							akpProperty += "";
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
							akpObject += "";
							trovatoTerzoDoppioCancelletto = true;
						}
					}
				}
			}
			
    		subjectsAkps.add(akpSubject);
    		propertiesAkps.add(akpProperty);
    		objectsAkps.add(akpObject);
			
			akpSubject = "";
			akpProperty = "";
			akpObject = "";
			trovatoPrimoDoppioCancelletto = false;
			trovatoSecondoDoppioCancelletto = false;
			trovatoTerzoDoppioCancelletto = false;
			
			line = reader.readLine();
		}
		
		reader.close();
		
		return subjectsAkps;
	}
	
	private static ArrayList <String> takeOnlyPropertiesOfObjectAkps (String pathFile) throws IOException
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
						akpSubject += "";
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
							akpProperty += "";
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
							akpObject += "";
							trovatoTerzoDoppioCancelletto = true;
						}
					}
				}
			}
			
			//if (!(akpSubject.equalsIgnoreCase("")))
    		//{
    		subjectsAkps.add(akpSubject);
    		//}
			//if (!(akpProperty.equalsIgnoreCase("")))
    		//{
    		propertiesAkps.add(akpProperty);
    		//}
			//if (!(akpObject.equalsIgnoreCase("")))
    		//{
    		objectsAkps.add(akpObject);
    		//}
			
			akpSubject = "";
			akpProperty = "";
			akpObject = "";
			trovatoPrimoDoppioCancelletto = false;
			trovatoSecondoDoppioCancelletto = false;
			trovatoTerzoDoppioCancelletto = false;
			
			line = reader.readLine();
		}
		
		reader.close();
		
		return propertiesAkps;
	}
	
	private static ArrayList <String> takeOnlyObjectsOfObjectAkps (String pathFile) throws IOException
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
						akpSubject += "";
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
							akpProperty += "";
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
							akpObject += "";
							trovatoTerzoDoppioCancelletto = true;
						}
					}
				}
			}
			
			//if (!(akpSubject.equalsIgnoreCase("")))
    		//{
    		subjectsAkps.add(akpSubject);
    		//}
			//if (!(akpProperty.equalsIgnoreCase("")))
    		//{
    		propertiesAkps.add(akpProperty);
    		//}
			//if (!(akpObject.equalsIgnoreCase("")))
    		//{
    		objectsAkps.add(akpObject);
    		//}
			
			akpSubject = "";
			akpProperty = "";
			akpObject = "";
			trovatoPrimoDoppioCancelletto = false;
			trovatoSecondoDoppioCancelletto = false;
			trovatoTerzoDoppioCancelletto = false;
			
			line = reader.readLine();
		}
		
		reader.close();
		
		return objectsAkps;
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
					subtypeOfObjectAkp += lineRead.charAt(i);
				}
				else
				{
					if (lineRead.charAt(i) == '#')
					{
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
	
	private static ArrayList <String> takeOnlyLocalNamesOfSubjectsOfObjectAkps (ArrayList <String> subjectsOfObjectsAkps)
	{
		String subjectOfObjectAkp = "";
		String localNameOfSubjectOfObjectAkp = "";
		ArrayList <String> localNamesOfSubjectsOfObjectAkps = new ArrayList <String> ();
		
		for (int i = 0; i < subjectsOfObjectsAkps.size(); i++) 
		{
			subjectOfObjectAkp = subjectsOfObjectsAkps.get(i);
					
			for (int j = 0; j < subjectOfObjectAkp.length(); j++) 
			{
				if (subjectOfObjectAkp.charAt(j) != '/')
				{
					localNameOfSubjectOfObjectAkp += subjectOfObjectAkp.charAt(j);
				}
				else
				{
					if (subjectOfObjectAkp.charAt(j) == '/')
					{
						localNameOfSubjectOfObjectAkp = "";
					}
				}
			}	
			localNamesOfSubjectsOfObjectAkps.add(localNameOfSubjectOfObjectAkp);
		}
		
		return localNamesOfSubjectsOfObjectAkps;
	}
	
	private static ArrayList <String> takeOnlyLocalNamesOfPropertiesOfObjectAkps (ArrayList <String> propertiesOfObjectAkps)
	{
		String propertyOfObjectAkp = "";
		String localNameOfPropertyOfObjectAkp = "";
		ArrayList <String> localNamesOfPropertiesOfObjectAkps = new ArrayList <String> ();
		
		for (int i = 0; i < propertiesOfObjectAkps.size(); i++) 
		{
			propertyOfObjectAkp = propertiesOfObjectAkps.get(i);
					
			for (int j = 0; j < propertyOfObjectAkp.length(); j++) 
			{
				if (propertyOfObjectAkp.charAt(j) != '/')
				{
					localNameOfPropertyOfObjectAkp += propertyOfObjectAkp.charAt(j);
				}
				else
				{
					if (propertyOfObjectAkp.charAt(j) == '/')
					{
						localNameOfPropertyOfObjectAkp = "";
					}
				}
			}	
			localNamesOfPropertiesOfObjectAkps.add(localNameOfPropertyOfObjectAkp);
		}
		
		return localNamesOfPropertiesOfObjectAkps;
	}
	
	private static ArrayList <String> takeOnlyLocalNamesOfObjectsOfObjectAkps (ArrayList <String> objectsOfObjectAkps)
	{
		String objectOfObjectAkp = "";
		String localNameOfObjectOfObjectAkp = "";
		ArrayList <String> localNamesOfObjectsOfObjectAkps = new ArrayList <String> ();
		
		for (int i = 0; i < objectsOfObjectAkps.size(); i++) 
		{
			objectOfObjectAkp = objectsOfObjectAkps.get(i);
					
			for (int j = 0; j < objectOfObjectAkp.length(); j++) 
			{
				if (objectOfObjectAkp.charAt(j) != '/')
				{
					localNameOfObjectOfObjectAkp += objectOfObjectAkp.charAt(j);
				}
				else
				{
					if (objectOfObjectAkp.charAt(j) == '/')
					{
						localNameOfObjectOfObjectAkp = "";
					}
				}
			}	
			localNamesOfObjectsOfObjectAkps.add(localNameOfObjectOfObjectAkp);
		}
		
		return localNamesOfObjectsOfObjectAkps;
	}
}