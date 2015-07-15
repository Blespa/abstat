package it.unimib.disco.summarization.output;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class InternalExternalObjectProperty
{
	public static void main(String[] args) throws FileNotFoundException, IOException
	{
		/*Receive two arguments from script (that are 'pathFile' and 'dataset').*/
		
		String pathFile = args[0];
		String dataset = args[1];
		
		/*Step: Decide if an object property is internal or external.*/
		
		objectPropertiesInternalExternal(pathFile,dataset);
	}
	
	private static void objectPropertiesInternalExternal(String pathFile, String dataset) throws FileNotFoundException, IOException
	{
		/*Per leggere da file .txt l'input.*/
		String fileObjectPropertiesPath = pathFile;
		BufferedReader brObjectProperties = new BufferedReader(new FileReader(fileObjectPropertiesPath));
		
		/*Per scrivere su file .txt l'output.*/
		FileWriter fwObjectProperties = new FileWriter("../data/summaries/"+dataset+"/patterns/count-object-properties-new.txt");
		BufferedWriter bwObjectProperties = new BufferedWriter(fwObjectProperties);
		
		/*Cuore dell'Algoritmo.*/
		boolean trovatoPrimoCancelletto = false;
		String lineRead = null;
		String objectProperty = "";
		String numberOfInstances = "";
		String typeOfObjectProperty = "";
		lineRead = brObjectProperties.readLine();
		
		while (lineRead != null)
		{
			for (int i = 0; i < lineRead.length(); i++)
			{
				if (trovatoPrimoCancelletto == false)
				{
					//Sto trovando l'object property.
					
					if ((lineRead.charAt(i) != '#') && (lineRead.charAt(i+1) != '#'))
					{
						objectProperty += lineRead.charAt(i);
					}
					if ((lineRead.charAt(i) != '#') && (lineRead.charAt(i+1) == '#'))
					{
						objectProperty += lineRead.charAt(i);
					}
					if ((lineRead.charAt(i) == '#') && (lineRead.charAt(i+1) != '#'))
					{
						objectProperty += lineRead.charAt(i);
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
			
			if (objectProperty.contains("http://dbpedia.org"))
			{
				typeOfObjectProperty = "internalObjectProperty";
			}
			else
			{
				typeOfObjectProperty = "externalObjectProperty";
			}
			
			//System.out.println("Object property found: " + objectProperty);
			//System.out.println("Number of instances of the object property '" + objectProperty + "': " + numberOfInstances);
			//System.out.println("Type of the object property '" + objectProperty + "': " + typeOfObjectProperty + "\n");
			
			bwObjectProperties.write(objectProperty);
			bwObjectProperties.write("##");
			bwObjectProperties.write(numberOfInstances);
			bwObjectProperties.write("##");
			bwObjectProperties.write(typeOfObjectProperty);
			bwObjectProperties.write("\n");
			
			objectProperty = "";
			numberOfInstances = "";
			typeOfObjectProperty = "";
			trovatoPrimoCancelletto = false;
			
			lineRead = brObjectProperties.readLine();
		}
		
		/*Chiudo le connessioni con i file.*/
		brObjectProperties.close();
		bwObjectProperties.close();
		fwObjectProperties.close();
	}
}
