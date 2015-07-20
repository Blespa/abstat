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
		String pathFile = args[0];
		String dataset = args[1];
		String payLevelDomain = args[2];
		
		objectPropertiesInternalExternal(pathFile,dataset,payLevelDomain);
	}
	
	private static void objectPropertiesInternalExternal(String pathFile, String dataset, String payLevelDomain) throws FileNotFoundException, IOException
	{
		String fileObjectPropertiesPath = pathFile;
		BufferedReader brObjectProperties = new BufferedReader(new FileReader(fileObjectPropertiesPath));
		
		FileWriter fwObjectProperties = new FileWriter("../data/summaries/"+dataset+"/patterns/count-object-properties-new.txt");
		BufferedWriter bwObjectProperties = new BufferedWriter(fwObjectProperties);
		
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
						objectProperty += "";
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
			
			if (objectProperty.contains(payLevelDomain))
			{
				typeOfObjectProperty = "internalObjectProperty";
			}
			else
			{
				typeOfObjectProperty = "externalObjectProperty";
			}
			
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
		
		brObjectProperties.close();
		bwObjectProperties.close();
		fwObjectProperties.close();
	}
}
