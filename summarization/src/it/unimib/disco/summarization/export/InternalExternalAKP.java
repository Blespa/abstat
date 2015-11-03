package it.unimib.disco.summarization.export;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;

public class InternalExternalAKP
{
	public static void main(String[] args) throws Exception
	{
		Events.summarization();
		
		try{
			String pathFile = args[0];
			String dataset = args[1];
			String payLevelDomain = args[2];
			
			decide(pathFile,dataset,payLevelDomain);
		}
		catch(Exception e){
			Events.summarization().error("", e);
		}
	}
	
	private static void decide(String pathFile, String dataset, String payLevelDomain) throws Exception
	{
		String fileAkpsPath = pathFile;
		BufferedReader brAkps = new BufferedReader(new FileReader(fileAkpsPath));
		
		FileWriter fwAkps = new FileWriter(pathFile.replace(".txt", "-new.txt"));
		BufferedWriter bwAkps = new BufferedWriter(fwAkps);
		
		boolean trovatoPrimoDoppioCancelletto = false;
		boolean trovatoSecondoDoppioCancelletto = false;
		boolean trovatoTerzoDoppioCancelletto = false;
		String lineRead = null;
		String subjectAkp = "";
		String propertyAkp = "";
		String objectAkp = "";
		String numberOfInstances = "";
		String typeOfAkp = "";
		
		lineRead = brAkps.readLine();
		
		while (lineRead != null)
		{
			for (int i = 0; i < lineRead.length()-4; i++)
			{
				if (trovatoPrimoDoppioCancelletto == false) 
				{
					if ((lineRead.charAt(i) != '#') && (lineRead.charAt(i+1) != '#'))
					{
						subjectAkp += lineRead.charAt(i);
					}
					if ((lineRead.charAt(i) != '#') && (lineRead.charAt(i+1) == '#'))
					{
						subjectAkp += lineRead.charAt(i);
					}
					if ((lineRead.charAt(i) == '#') && (lineRead.charAt(i+1) != '#'))
					{
						subjectAkp += lineRead.charAt(i);
					}
					if ((lineRead.charAt(i) == '#') && (lineRead.charAt(i+1) == '#'))
					{
						subjectAkp += "";
						trovatoPrimoDoppioCancelletto = true;
					}
				}
				
				if (trovatoPrimoDoppioCancelletto == true)
				{
					if (trovatoSecondoDoppioCancelletto == false)
					{
						if ((lineRead.charAt(i+2) != '#') && (lineRead.charAt(i+3) != '#'))
						{
							propertyAkp += lineRead.charAt(i+2);
						}
						if ((lineRead.charAt(i+2) != '#') && (lineRead.charAt(i+3) == '#'))
						{
							propertyAkp += lineRead.charAt(i+2);
						}
						if ((lineRead.charAt(i+2) == '#') && (lineRead.charAt(i+3) != '#'))
						{
							propertyAkp += lineRead.charAt(i+2);
						}
						if ((lineRead.charAt(i+2) == '#') && (lineRead.charAt(i+3) == '#'))
						{
							propertyAkp += "";
							trovatoSecondoDoppioCancelletto = true;
						}
					}
					if (trovatoSecondoDoppioCancelletto == true)
					{
						if (trovatoTerzoDoppioCancelletto == false)
						{
							if ((lineRead.charAt(i+4) != '#') && (lineRead.charAt(i+5) != '#'))
							{
								objectAkp += lineRead.charAt(i+4);
							}
							if ((lineRead.charAt(i+4) != '#') && (lineRead.charAt(i+5) == '#'))
							{
								objectAkp += lineRead.charAt(i+4);
							}
							if ((lineRead.charAt(i+4) == '#') && (lineRead.charAt(i+5) != '#'))
							{
								objectAkp += lineRead.charAt(i+4);
							}
							if ((lineRead.charAt(i+4) == '#') && (lineRead.charAt(i+5) == '#'))
							{
								objectAkp += "";
								trovatoTerzoDoppioCancelletto = true;
							}
						}
						if (trovatoTerzoDoppioCancelletto == true)
						{
							if (lineRead.charAt(i+4) != '#')
							{
								numberOfInstances += lineRead.charAt(i+4);
							}
						}
					}
				}
			}
			
			if (((!(subjectAkp.contains("wikidata"))) && (subjectAkp.contains(payLevelDomain))) && ((!(objectAkp.contains("wikidata"))) && (objectAkp.contains(payLevelDomain))))
			{
				typeOfAkp = "internal";
			}
			else
			{
				typeOfAkp = "external";
			}
			
			bwAkps.write(subjectAkp);
			bwAkps.write("##");
			bwAkps.write(propertyAkp);
			bwAkps.write("##");
			bwAkps.write(objectAkp);
			bwAkps.write("##");
			bwAkps.write(numberOfInstances);
			bwAkps.write("##");
			bwAkps.write(typeOfAkp);
			bwAkps.write("\n");
			
			subjectAkp = "";
			propertyAkp = "";
			objectAkp = "";
			numberOfInstances = "";
			typeOfAkp = "";
			trovatoPrimoDoppioCancelletto = false;
			trovatoSecondoDoppioCancelletto = false;
			trovatoTerzoDoppioCancelletto = false;
			
			lineRead = brAkps.readLine();
		}
		
		brAkps.close();
		bwAkps.close();
		fwAkps.close();
	}
}
