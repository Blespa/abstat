package it.unimib.disco.summarization.output;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class InternalExternalConcept
{
	public static void main(String[] args) throws FileNotFoundException, IOException
	{
		String pathFile = args[0];
		String dataset = args[1];
		String payLevelDomain = args[2];
		
		conceptsInternalExternal(pathFile,dataset, payLevelDomain);
	}
	
	private static void conceptsInternalExternal(String pathFile, String dataset, String payLevelDomain) throws FileNotFoundException, IOException
	{
		String fileConceptsPath = pathFile;
		BufferedReader brConcepts = new BufferedReader(new FileReader(fileConceptsPath));
		
		FileWriter fwConcepts = new FileWriter("../data/summaries/"+dataset+"/patterns/count-concepts-new.txt");
		BufferedWriter bwConcepts = new BufferedWriter(fwConcepts);
		
		boolean trovatoPrimoCancelletto = false;
		String lineRead = null;
		String concept = "";
		String numberOfInstances = "";
		String typeOfConcept = "";
		lineRead = brConcepts.readLine();
		
		while (lineRead != null)
		{
			for (int i = 0; i < lineRead.length(); i++)
			{
				if (trovatoPrimoCancelletto == false)
				{
					if ((lineRead.charAt(i) != '#') && (lineRead.charAt(i+1) != '#'))
					{
						concept += lineRead.charAt(i);
					}
					if ((lineRead.charAt(i) != '#') && (lineRead.charAt(i+1) == '#'))
					{
						concept += lineRead.charAt(i);
					}
					if ((lineRead.charAt(i) == '#') && (lineRead.charAt(i+1) != '#'))
					{
						concept += lineRead.charAt(i);
					}
					if ((lineRead.charAt(i) == '#') && (lineRead.charAt(i+1) == '#'))
					{
						concept += "";
						trovatoPrimoCancelletto = true;
					}
				}
				
				if (trovatoPrimoCancelletto == true)
				{
					if (lineRead.charAt(i) != '#')
					{
						numberOfInstances += lineRead.charAt(i);
					}
				}
			}
			
			if (concept.contains(payLevelDomain))
			{
				typeOfConcept = "internalConcept";
			}
			else
			{
				typeOfConcept = "externalConcept";
			}
			
			bwConcepts.write(concept);
			bwConcepts.write("##");
			bwConcepts.write(numberOfInstances);
			bwConcepts.write("##");
			bwConcepts.write(typeOfConcept);
			bwConcepts.write("\n");
			
			concept = "";
			numberOfInstances = "";
			typeOfConcept = "";
			trovatoPrimoCancelletto = false;
			
			lineRead = brConcepts.readLine();
		}
		
		brConcepts.close();
		bwConcepts.close();
		fwConcepts.close();
	}
}
