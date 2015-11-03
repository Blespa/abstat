package it.unimib.disco.summarization.export;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;

public class InternalExternalObjectAkp
{
	public static void main(String[] args) throws Exception
	{
		Events.summarization();
		
		try{
			String pathFile = args[0];
			String dataset = args[1];
			String payLevelDomain = args[2];
			
			objectAkpInternalExternal(pathFile,dataset,payLevelDomain);
		}
		catch(Exception e){
			Events.summarization().error("", e);
		}
	}
	
	private static void objectAkpInternalExternal(String pathFile, String dataset, String payLevelDomain) throws Exception
	{
		String fileObjectAkpsPath = pathFile;
		BufferedReader brObjectAkps = new BufferedReader(new FileReader(fileObjectAkpsPath));
		
		FileWriter fwObjectAkps = new FileWriter("../data/summaries/"+dataset+"/patterns/object-akp-new.txt");
		BufferedWriter bwObjectAkps = new BufferedWriter(fwObjectAkps);
		
		boolean trovatoPrimoDoppioCancelletto = false;
		boolean trovatoSecondoDoppioCancelletto = false;
		boolean trovatoTerzoDoppioCancelletto = false;
		String lineRead = null;
		String subjectObjectAkp = "";
		String propertyObjectAkp = "";
		String objectObjectAkp = "";
		String numberOfInstances = "";
		String typeOfObjectAkp = "";
		
		lineRead = brObjectAkps.readLine();
		
		while (lineRead != null)
		{
			for (int i = 0; i < lineRead.length()-4; i++)
			{
				if (trovatoPrimoDoppioCancelletto == false) 
				{
					if ((lineRead.charAt(i) != '#') && (lineRead.charAt(i+1) != '#'))
					{
						subjectObjectAkp += lineRead.charAt(i);
					}
					if ((lineRead.charAt(i) != '#') && (lineRead.charAt(i+1) == '#'))
					{
						subjectObjectAkp += lineRead.charAt(i);
					}
					if ((lineRead.charAt(i) == '#') && (lineRead.charAt(i+1) != '#'))
					{
						subjectObjectAkp += lineRead.charAt(i);
					}
					if ((lineRead.charAt(i) == '#') && (lineRead.charAt(i+1) == '#'))
					{
						subjectObjectAkp += "";
						trovatoPrimoDoppioCancelletto = true;
					}
				}
				
				if (trovatoPrimoDoppioCancelletto == true)
				{
					if (trovatoSecondoDoppioCancelletto == false)
					{
						if ((lineRead.charAt(i+2) != '#') && (lineRead.charAt(i+3) != '#'))
						{
							propertyObjectAkp += lineRead.charAt(i+2);
						}
						if ((lineRead.charAt(i+2) != '#') && (lineRead.charAt(i+3) == '#'))
						{
							propertyObjectAkp += lineRead.charAt(i+2);
						}
						if ((lineRead.charAt(i+2) == '#') && (lineRead.charAt(i+3) != '#'))
						{
							propertyObjectAkp += lineRead.charAt(i+2);
						}
						if ((lineRead.charAt(i+2) == '#') && (lineRead.charAt(i+3) == '#'))
						{
							propertyObjectAkp += "";
							trovatoSecondoDoppioCancelletto = true;
						}
					}
					if (trovatoSecondoDoppioCancelletto == true)
					{
						if (trovatoTerzoDoppioCancelletto == false)
						{
							if ((lineRead.charAt(i+4) != '#') && (lineRead.charAt(i+5) != '#'))
							{
								objectObjectAkp += lineRead.charAt(i+4);
							}
							if ((lineRead.charAt(i+4) != '#') && (lineRead.charAt(i+5) == '#'))
							{
								objectObjectAkp += lineRead.charAt(i+4);
							}
							if ((lineRead.charAt(i+4) == '#') && (lineRead.charAt(i+5) != '#'))
							{
								objectObjectAkp += lineRead.charAt(i+4);
							}
							if ((lineRead.charAt(i+4) == '#') && (lineRead.charAt(i+5) == '#'))
							{
								objectObjectAkp += "";
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
			
			if (((!(subjectObjectAkp.contains("wikidata"))) && (subjectObjectAkp.contains(payLevelDomain))) && ((!(objectObjectAkp.contains("wikidata"))) && (objectObjectAkp.contains(payLevelDomain))))
			{
				typeOfObjectAkp = "internal";
			}
			else
			{
				typeOfObjectAkp = "external";
			}
			
			bwObjectAkps.write(subjectObjectAkp);
			bwObjectAkps.write("##");
			bwObjectAkps.write(propertyObjectAkp);
			bwObjectAkps.write("##");
			bwObjectAkps.write(objectObjectAkp);
			bwObjectAkps.write("##");
			bwObjectAkps.write(numberOfInstances);
			bwObjectAkps.write("##");
			bwObjectAkps.write(typeOfObjectAkp);
			bwObjectAkps.write("\n");
			
			subjectObjectAkp = "";
			propertyObjectAkp = "";
			objectObjectAkp = "";
			numberOfInstances = "";
			typeOfObjectAkp = "";
			trovatoPrimoDoppioCancelletto = false;
			trovatoSecondoDoppioCancelletto = false;
			trovatoTerzoDoppioCancelletto = false;
			
			lineRead = brObjectAkps.readLine();
		}
		
		brObjectAkps.close();
		bwObjectAkps.close();
		fwObjectAkps.close();
	}
}
