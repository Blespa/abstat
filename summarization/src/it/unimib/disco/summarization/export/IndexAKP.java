package it.unimib.disco.summarization.export;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrInputDocument;

public class IndexAKP
{
	public static void main(String[] args) throws Exception
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
			
			datatypeAkpsImport(client,pathFile,dataset,type);
		}
		catch(Exception e){
			Events.summarization().error("", e);
		}
	}

	private static void datatypeAkpsImport (HttpSolrServer client, String pathFile, String dataset, String type) throws Exception
	{
		ArrayList <String> subjects = subjects(pathFile);
		ArrayList <String> properties = properties(pathFile);
		ArrayList <String> objects = objects(pathFile);
		
		ArrayList <String> subtypes = subtypes(pathFile);
		
		ArrayList <String> subjectsLocalNames = takeLocalNamesOnly(subjects);
		ArrayList <String> propertiesLocalNames = takeLocalNamesOnly(properties);
		ArrayList <String> objectsLocalNames = takeLocalNamesOnly(objects);
		
		ArrayList<Long> occurrences = selectOccurrences(pathFile);
		
		index(client,subjects,properties,objects,subjectsLocalNames,propertiesLocalNames,objectsLocalNames,subtypes,dataset, occurrences, type);
	}
	
	private static ArrayList<Long> selectOccurrences(String pathFile) throws Exception {
		ArrayList<Long> result = new ArrayList<Long>();
		LineIterator lines = FileUtils.lineIterator(new File(pathFile));
		while(lines.hasNext()){
			String line = lines.next();
			result.add(Long.parseLong(line.split("##")[3]));
		}
		return result;
	}

	private static void index (HttpSolrServer client, ArrayList <String> subjectsOfDatatypeAkps, ArrayList <String> propertiesOfDatatypeAkps, ArrayList <String> objectsOfDatatypeAkps, ArrayList <String> localNamesOfSubjectsOfDatatypeAkps, ArrayList <String> localNamesOfPropertiesOfDatatypeAkps, ArrayList <String> localNamesOfObjectsOfDatatypeAkps, ArrayList <String> subtypeOfDatatypeAkps, String dataset, ArrayList<Long> occurrences, String type) throws Exception
	{
		int numberOfDatatypeAkps = subjectsOfDatatypeAkps.size();
		
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
			
			doc.setField("URI", akp);
			doc.setField("type", type);
			doc.setField("dataset", dataset);
			doc.setField("subtype", subtypeOfDatatypeAkp);
			doc.setField("fullTextSearchField", localNameAkp);
			doc.setField("occurrence", occurrences.get(i));
			
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
	
	private static ArrayList <String> subjects (String pathFile) throws IOException
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
	
	private static ArrayList <String> properties (String pathFile) throws IOException
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
		
		return propertiesAkps;
	}
	
	private static ArrayList <String> objects (String pathFile) throws IOException
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
		
		return objectsAkps;
	}
	
	private static ArrayList <String> subtypes(String pathFile) throws FileNotFoundException, IOException
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
					subtypeOfDatatypeAkp += lineRead.charAt(i);
				}
				else
				{
					if (lineRead.charAt(i) == '#')
					{
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
	
	private static ArrayList <String> takeLocalNamesOnly(ArrayList <String> subjectsOfDatatypeAkps)
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
}