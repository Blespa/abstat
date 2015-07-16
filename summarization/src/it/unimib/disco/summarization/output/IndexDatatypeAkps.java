package it.unimib.disco.summarization.output;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
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
		String dataset = args[3];
		
		/*Step: Datatype-akp import.*/
		
		String serverUrl = "http://"+host+":"+port+"/solr/indexing";
		HttpSolrServer client = new HttpSolrServer(serverUrl);
		
		datatypeAkpsImport(client,pathFile,dataset);
	}

	private static void datatypeAkpsImport (HttpSolrServer client, String pathFile, String dataset) throws IOException, SolrServerException
	{
		ArrayList <String> subjectsOfDatatypeAkps = takeOnlySubjectOfDatatypeAkps(pathFile);
		ArrayList <String> propertiesOfDatatypeAkps = takeOnlyPropertiesOfDatatypeAkps(pathFile);
		ArrayList <String> objectsOfDatatypeAkps = takeOnlyObjectsOfDatatypeAkps(pathFile);
		
		ArrayList <String> localNamesOfSubjectsOfDatatypeAkps = takeOnlyLocalNamesOfSubjectsOfDatatypeAkps(subjectsOfDatatypeAkps);
		ArrayList <String> localNamesOfPropertiesOfDatatypeAkps = takeOnlyLocalNamesOfPropertiesOfDatatypeAkps(propertiesOfDatatypeAkps);
		ArrayList <String> localNamesOfObjectsOfDatatypeAkps = takeOnlyLocalNamesOfObjectsOfDatatypeAkps(objectsOfDatatypeAkps);
		
		ArrayList <String> subtypeOfDatatypeAkps = takeOnlySubtypeOfDatatypeAkps(pathFile);
		
		indexDatatypeAkps(client,subjectsOfDatatypeAkps,propertiesOfDatatypeAkps,objectsOfDatatypeAkps,localNamesOfSubjectsOfDatatypeAkps,localNamesOfPropertiesOfDatatypeAkps,localNamesOfObjectsOfDatatypeAkps,subtypeOfDatatypeAkps,dataset);
	}

	private static void indexDatatypeAkps (HttpSolrServer client, ArrayList <String> subjectsOfDatatypeAkps, ArrayList <String> propertiesOfDatatypeAkps, ArrayList <String> objectsOfDatatypeAkps, ArrayList <String> localNamesOfSubjectsOfDatatypeAkps, ArrayList <String> localNamesOfPropertiesOfDatatypeAkps, ArrayList <String> localNamesOfObjectsOfDatatypeAkps, ArrayList <String> subtypeOfDatatypeAkps, String dataset) throws SolrServerException, IOException
	{
		int numberOfDatatypeAkps = subjectsOfDatatypeAkps.size(); //i primi tre ArrayList passati hanno lunghezza uguale
		
		for (int i = 0; i < numberOfDatatypeAkps; i++)
		{
			String subjectOfDatatypeAkp = subjectsOfDatatypeAkps.get(i);
			String propertyOfDatatypeAkp = propertiesOfDatatypeAkps.get(i);
			String objectOfDatatypeAkp = objectsOfDatatypeAkps.get(i);
			
			String localNameOfSubjectOfDatatypeAkp = localNamesOfSubjectsOfDatatypeAkps.get(i);
			String localNameOfPropertyOfDatatypeAkp = localNamesOfPropertiesOfDatatypeAkps.get(i);
			String localNameOfObjectOfDatatypeAkp = localNamesOfObjectsOfDatatypeAkps.get(i);
			
			String subtypeOfDatatypeAkp = subtypeOfDatatypeAkps.get(i);
			
			String[] akp = new String[3];
			akp[0] = subjectOfDatatypeAkp;
			akp[1] = propertyOfDatatypeAkp;
			akp[2] = objectOfDatatypeAkp;
			
			String[] localNameAkp = new String[3];
			localNameAkp[0] = localNameOfSubjectOfDatatypeAkp;
			localNameAkp[1] = localNameOfPropertyOfDatatypeAkp;
			localNameAkp[2] = localNameOfObjectOfDatatypeAkp;
			
			SolrInputDocument doc = new SolrInputDocument();
			doc.setField("idDocument", (i+1+20+11+5));
			doc.setField("URI", akp);
			doc.setField("type", "datatypeAkp");
			doc.setField("dataset", dataset);
			doc.setField("subtype", subtypeOfDatatypeAkp);
			doc.setField("fullTextSearchField", localNameAkp);
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
	
	private static ArrayList <String> takeOnlySubjectOfDatatypeAkps (String pathFile) throws IOException
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
		
		return subjectsAkps;
		
		/*ArrayList <String> akps = new ArrayList <String> ();
		
		for (int i = 0; i < subjectsAkps.size(); i++)
		{
			String akp = subjectsAkps.get(i)+","+propertiesAkps.get(i)+","+objectsAkps.get(i);
			akps.add(akp);
		}
		
		return akps;*/
	}
	
	private static ArrayList <String> takeOnlyPropertiesOfDatatypeAkps (String pathFile) throws IOException
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
		
		return propertiesAkps;
		
		/*ArrayList <String> akps = new ArrayList <String> ();
		
		for (int i = 0; i < subjectsAkps.size(); i++)
		{
			String akp = subjectsAkps.get(i)+","+propertiesAkps.get(i)+","+objectsAkps.get(i);
			akps.add(akp);
		}
		
		return akps;*/
	}
	
	private static ArrayList <String> takeOnlyObjectsOfDatatypeAkps (String pathFile) throws IOException
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
		
		return objectsAkps;
		
		/*ArrayList <String> akps = new ArrayList <String> ();
		
		for (int i = 0; i < subjectsAkps.size(); i++)
		{
			String akp = subjectsAkps.get(i)+","+propertiesAkps.get(i)+","+objectsAkps.get(i);
			akps.add(akp);
		}
		
		return akps;*/
	}
	
	private static ArrayList <String> takeOnlySubtypeOfDatatypeAkps(String pathFile) throws FileNotFoundException, IOException
	{
		String path = pathFile;
		BufferedReader reader = new BufferedReader(new FileReader(path));
		
		ArrayList <String> subtypeOfDatatypeAkps = new ArrayList <String> ();
		String subtypeOfDatatypeAkp = "";
		int contatore = 0;
    	
    	String lineRead = reader.readLine();
    	
    	while ((lineRead != null) && (contatore < lineRead.length()))
		{
			for (int i = 0; i < lineRead.length(); i++)
			{
				if (lineRead.charAt(i) != '#')
				{
					//System.out.println("sono dentro il primo if");
					subtypeOfDatatypeAkp += lineRead.charAt(i);
				}
				else
				{
					if (lineRead.charAt(i) == '#')
					{
						//System.out.println("sono dentro il secondo if");
						subtypeOfDatatypeAkp = "";
					}
				}
				contatore++;
			}
			
			contatore = 0;
			
			subtypeOfDatatypeAkps.add(subtypeOfDatatypeAkp);
			
			lineRead = reader.readLine();
		}
    	
    	reader.close();
    	
		return subtypeOfDatatypeAkps;
	}
	
	private static ArrayList <String> takeOnlyLocalNamesOfSubjectsOfDatatypeAkps (ArrayList <String> subjectsOfDatatypeAkps)
	{
		String subjectOfDatatypeAkp = "";
		String localNameOfSubjectOfDatatypeAkp = "";
		ArrayList <String> localNamesOfSubjectsOfDatatypeAkps = new ArrayList <String> ();
		
		for (int i = 0; i < subjectsOfDatatypeAkps.size(); i++) 
		{
			subjectOfDatatypeAkp = subjectsOfDatatypeAkps.get(i);
					
			for (int j = 0; j < subjectOfDatatypeAkp.length(); j++) 
			{
				if (subjectOfDatatypeAkp.charAt(j) != '/')
				{
					localNameOfSubjectOfDatatypeAkp += subjectOfDatatypeAkp.charAt(j);
				}
				else
				{
					if (subjectOfDatatypeAkp.charAt(j) == '/')
					{
						localNameOfSubjectOfDatatypeAkp = "";
					}
				}
			}	
			localNamesOfSubjectsOfDatatypeAkps.add(localNameOfSubjectOfDatatypeAkp);
		}
		
		return localNamesOfSubjectsOfDatatypeAkps;
	}
	
	private static ArrayList <String> takeOnlyLocalNamesOfPropertiesOfDatatypeAkps (ArrayList <String> propertiesOfDatatypeAkps)
	{
		String propertyOfDatatypeAkp = "";
		String localNameOfPropertyOfDatatypeAkp = "";
		ArrayList <String> localNamesOfPropertiesOfDatatypeAkps = new ArrayList <String> ();
		
		for (int i = 0; i < propertiesOfDatatypeAkps.size(); i++) 
		{
			propertyOfDatatypeAkp = propertiesOfDatatypeAkps.get(i);
					
			for (int j = 0; j < propertyOfDatatypeAkp.length(); j++) 
			{
				if (propertyOfDatatypeAkp.charAt(j) != '/')
				{
					localNameOfPropertyOfDatatypeAkp += propertyOfDatatypeAkp.charAt(j);
				}
				else
				{
					if (propertyOfDatatypeAkp.charAt(j) == '/')
					{
						localNameOfPropertyOfDatatypeAkp = "";
					}
				}
			}	
			localNamesOfPropertiesOfDatatypeAkps.add(localNameOfPropertyOfDatatypeAkp);
		}
		
		return localNamesOfPropertiesOfDatatypeAkps;
	}
	
	private static ArrayList <String> takeOnlyLocalNamesOfObjectsOfDatatypeAkps (ArrayList <String> objectsOfDatatypeAkps)
	{
		String objectOfDatatypeAkp = "";
		String localNameOfObjectOfDatatypeAkp = "";
		ArrayList <String> localNamesOfObjectsOfDatatypeAkps = new ArrayList <String> ();
		
		for (int i = 0; i < objectsOfDatatypeAkps.size(); i++) 
		{
			objectOfDatatypeAkp = objectsOfDatatypeAkps.get(i);
					
			for (int j = 0; j < objectOfDatatypeAkp.length(); j++) 
			{
				if (objectOfDatatypeAkp.charAt(j) != '/')
				{
					localNameOfObjectOfDatatypeAkp += objectOfDatatypeAkp.charAt(j);
				}
				else
				{
					if (objectOfDatatypeAkp.charAt(j) == '/')
					{
						localNameOfObjectOfDatatypeAkp = "";
					}
				}
			}	
			localNamesOfObjectsOfDatatypeAkps.add(localNameOfObjectOfDatatypeAkp);
		}
		
		return localNamesOfObjectsOfDatatypeAkps;
	}
}