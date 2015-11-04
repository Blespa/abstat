package it.unimib.disco.summarization.export;

import it.unimib.disco.summarization.ontology.TypeOf;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;

public class InternalExternalResources
{
	public static void main(String[] args) throws Exception
	{
		Events.summarization();
		
		try{
			String pathFile = args[0];
			String dataset = args[1];
			String payLevelDomain = args[2];
			decide(pathFile,dataset, payLevelDomain);
		}
		catch(Exception e){
			Events.summarization().error("", e);
		}
	}
	
	private static void decide(String path, String dataset, String domain) throws Exception
	{
		BufferedReader brConcepts = new BufferedReader(new FileReader(path));
		FileWriter fwConcepts = new FileWriter(path.replace(".txt", "-new.txt"));
		BufferedWriter bwConcepts = new BufferedWriter(fwConcepts);
		
		boolean trovatoPrimoCancelletto = false;
		String concept = "";
		String numberOfInstances = "";
		String lineRead = brConcepts.readLine();
		TypeOf internalResources = new TypeOf(domain);
		
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
			
			bwConcepts.write(concept);
			bwConcepts.write("##");
			bwConcepts.write(numberOfInstances);
			bwConcepts.write("##");
			bwConcepts.write(internalResources.resource(concept));
			bwConcepts.write("\n");
			
			concept = "";
			numberOfInstances = "";
			trovatoPrimoCancelletto = false;
			
			lineRead = brConcepts.readLine();
		}
		
		brConcepts.close();
		bwConcepts.close();
		fwConcepts.close();
	}
}
