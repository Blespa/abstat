package it.unimib.disco.summarization.starter;

/**
 * The Class Laucher: used in order to test Starter execution
 */
public class Launcher {
	
	public static void main(String[] args) {
		
		
		String [] arguments = new String[3];
		
		arguments[0]="/Users/anisarula/Documents/ontology/";
		arguments[1]="/Users/anisarula/Dropbox (Personal)/profiling/SchemaSummaries.org/Source Code/Utility/SchemaSummaries_Data_Extraction/Reports/";
		arguments[2]="/Users/anisarula/Dropbox (Personal)/profiling/SchemaSummaries.org/Source Code/Utility/SchemaSummaries_Data_Extraction/Reports/Tmp_Data_For_Computation/";
		
		ReadingRDFdata.main(arguments);
		
		/*
		String [] arguments = new String[1];
		
		arguments[0] = "Archaea site:en.wikipedia.org";
		
		System.out.println(GoogleSearch.getFirstParagraphFromWikipediaLink(arguments));
		*/
	}
}
