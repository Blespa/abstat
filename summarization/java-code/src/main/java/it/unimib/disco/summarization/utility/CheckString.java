package it.unimib.disco.summarization.utility;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The Class CheckString.
 */
public class CheckString 

{

	/**
	 * Questa funzione restituisce true se la stringa
	 * rispetta l'espressione regolare.
	 * 
	 * @param regex espressione regolare
	 * @param input stringa
	 * 
	 * @return restituisce true o false
	 * 
	 */
	public static boolean IsValid(String regex, String input)
	{
		  Pattern pattern = Pattern.compile(regex);
		  Matcher matcher = pattern.matcher(input);

		  if (matcher.matches())
		    return true;
		  else
		    return false;
	} 
}
