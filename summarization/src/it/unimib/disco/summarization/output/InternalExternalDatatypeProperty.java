package it.unimib.disco.summarization.output;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class InternalExternalDatatypeProperty
{
	public static void main(String[] args) throws FileNotFoundException, IOException
	{
		/*Receive two arguments from script (that are 'pathFile' and 'dataset').*/
		
		String pathFile = args[0];
		String dataset = args[1];
		
		/*Step: Decide if a datatype property is internal or external.*/
		
		datatypePropertiesInternalExternal(pathFile,dataset);
	}
	
	private static void datatypePropertiesInternalExternal(String pathFile, String dataset) throws FileNotFoundException, IOException
	{
		/*Per leggere da file .txt l'input.*/
		String fileDatatypePropertiesPath = pathFile;
		BufferedReader brDatatypeProperties = new BufferedReader(new FileReader(fileDatatypePropertiesPath));
		
		/*Per scrivere su file .txt l'output.*/
		FileWriter fwDatatypeProperties = new FileWriter("../data/summaries/"+dataset+"/patterns/count-datatype-properties-new.txt");
		BufferedWriter bwDatatypeProperties = new BufferedWriter(fwDatatypeProperties);
		
		/*Cuore dell'Algoritmo.*/
		boolean trovatoPrimoCancelletto = false;
		String lineRead = null;
		String datatypeProperty = "";
		String numberOfInstances = "";
		String typeOfDatatypeProperty = "";
		lineRead = brDatatypeProperties.readLine();
		
		while (lineRead != null)
		{
			for (int i = 0; i < lineRead.length(); i++)
			{
				if (trovatoPrimoCancelletto == false)
				{
					//Sto trovando la datatype property.
					
					if ((lineRead.charAt(i) != '#') && (lineRead.charAt(i+1) != '#'))
					{
						datatypeProperty += lineRead.charAt(i);
					}
					if ((lineRead.charAt(i) != '#') && (lineRead.charAt(i+1) == '#'))
					{
						datatypeProperty += lineRead.charAt(i);
					}
					if ((lineRead.charAt(i) == '#') && (lineRead.charAt(i+1) != '#'))
					{
						datatypeProperty += lineRead.charAt(i);
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
			
			if (datatypeProperty.contains("http://dbpedia.org"))
			{
				typeOfDatatypeProperty = "internalDatatypeProperty";
			}
			else
			{
				typeOfDatatypeProperty = "externalDatatypeProperty";
			}
			
			//System.out.println("Datatype property found: " + datatypeProperty);
			//System.out.println("Number of instances of the datatype property '" + datatypeProperty + "': " + numberOfInstances);
			//System.out.println("Type of the datatype property '" + datatypeProperty + "': " + typeOfDatatypeProperty + "\n");
			
			bwDatatypeProperties.write(datatypeProperty);
			bwDatatypeProperties.write("##");
			bwDatatypeProperties.write(numberOfInstances);
			bwDatatypeProperties.write("##");
			bwDatatypeProperties.write(typeOfDatatypeProperty);
			bwDatatypeProperties.write("\n");
			
			datatypeProperty = "";
			numberOfInstances = "";
			typeOfDatatypeProperty = "";
			trovatoPrimoCancelletto = false;
			
			lineRead = brDatatypeProperties.readLine();
		}
		
		/*Chiudo le connessioni con i file.*/
		brDatatypeProperties.close();
		bwDatatypeProperties.close();
		fwDatatypeProperties.close();
	}
}
