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
		/*Receive two arguments from script (that are 'pathFile' and 'dataset').*/
		
		String pathFile = args[0];
		String dataset = args[1];
		
		/*Step: Decide if a concept is internal or external.*/
		
		conceptsInternalExternal(pathFile,dataset);
	}
	
	private static void conceptsInternalExternal(String pathFile, String dataset) throws FileNotFoundException, IOException
	{
		/*Per leggere da file .txt l'input.*/
		String fileConceptsPath = pathFile;
		BufferedReader brConcepts = new BufferedReader(new FileReader(fileConceptsPath));
		
		/*Per scrivere su file .txt l'output.*/
		FileWriter fwConcepts = new FileWriter("../data/summaries/"+dataset+"/patterns/count-concepts-new.txt");
		BufferedWriter bwConcepts = new BufferedWriter(fwConcepts);
		
		/*Cuore dell'Algoritmo.*/
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
					//Sto trovando il concetto.
					
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
			
			if (concept.contains("http://dbpedia.org"))
			{
				typeOfConcept = "internalConcept";
			}
			else
			{
				typeOfConcept = "externalConcept";
			}
			
			//System.out.println("Concepts found: " + concept);
			//System.out.println("Number of instances of the concept '" + concept + "': " + numberOfInstances);
			//System.out.println("Type of the concept '" + concept + "': " + typeOfConcept + "\n");
			
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
		
		/*Chiudo le connessioni con i file.*/
		brConcepts.close();
		bwConcepts.close();
		fwConcepts.close();
	}
}
