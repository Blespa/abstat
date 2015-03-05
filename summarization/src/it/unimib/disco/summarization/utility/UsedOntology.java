package it.unimib.disco.summarization.utility;

import it.unimib.disco.summarization.datatype.Concept;
import it.unimib.disco.summarization.datatype.EquProperty;
import it.unimib.disco.summarization.datatype.Property;
import it.unimib.disco.summarization.starter.Events;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.OntResource;

/**
 * Extract imported or used Ontology from a Ontology Model
 */
public class UsedOntology {
	
	private static List<String> impOnt = new ArrayList<String>();
	
	public static List<String> getUsedOntology(OntModel ontologyModel, Concept concepts, Property properties, EquProperty equProperties){
		
		Set<String> tempimpOnt;
		
		//Get Explicit Imported Ontology
		tempimpOnt = ontologyModel.listImportedOntologyURIs();
		
		for(String Ont : tempimpOnt){
			impOnt.add(Ont);
		}
		
		//Read Used Ontology From Concept and Property
		Iterator<OntResource> cIter = concepts.getExtractedConcepts().iterator();
		
		while (cIter.hasNext()) {
			OntResource key = cIter.next();
			
			new Events().debug(key);

			String ontUri = key.getURI().replace(key.getLocalName(), "").replace("#", "");
			int isSub = isSubstring(impOnt, ontUri); //Check if string is substring of another

			if(!impOnt.contains(ontUri) && isSub==impOnt.size()){ //If is new Used Ontology
				impOnt.add(ontUri);
			}
			else if(!impOnt.contains(ontUri) && isSub!=-1){ //If is new Used Ontology and Substring
				//Remove previous saved ontology and add new one
				impOnt.remove(isSub);			
			}
			
		}
		
		Iterator<Entry<OntProperty, List<OntProperty>>> eqIter = equProperties.getExtractedEquProperty().entrySet().iterator();

		while (eqIter.hasNext()) {
			Entry<OntProperty, List<OntProperty>> pairs = eqIter.next();
			List<OntProperty> value = pairs.getValue();
			
			Iterator<OntProperty> equPropIt = value.iterator();
			
			while (equPropIt.hasNext()) {
				
				OntProperty eqProp = equPropIt.next();
				
				String ontUri = eqProp.getURI().replace(eqProp.getLocalName(), "").replace("#", "");
				int isSub = isSubstring(impOnt, ontUri); //Check if string is substring of another
	
				if(!impOnt.contains(ontUri) && isSub==impOnt.size()){ //If is new Used Ontology
					impOnt.add(ontUri);
				}
				else if(!impOnt.contains(ontUri) && isSub!=-1){ //If is new Used Ontology and Substring
					//Remove previous saved ontology and add new one
					impOnt.remove(isSub);			
				}
			}

		}
		
		Iterator<OntProperty> pIter = properties.getExtractedProperty().iterator();

		while (pIter.hasNext()) {
			OntProperty key = pIter.next();

			String ontUri = key.getURI().replace(key.getLocalName(), "").replace("#", "");
			int isSub = isSubstring(impOnt, ontUri); //Check if string is substring of another

			if(!impOnt.contains(ontUri) && isSub==impOnt.size()){ //If is new Used Ontology
				impOnt.add(ontUri);
			}
			else if(!impOnt.contains(ontUri) && isSub!=-1){ //If is new Used Ontology and Substring
				//Remove previous saved ontology and add new one
				impOnt.remove(isSub);			
			}

		}
		
		return impOnt;
	}
	
	//Search for substring in Set
	private static int isSubstring(List<String> StringSet, String SubString){
		
		int posMatch = 0; //Save position of match if there is
		
		for(String impOnt : StringSet){
			//Se le stringhe sono uguali
			if(impOnt.equals(SubString))
				return -1;
			//Se la stringa � sottostringa di una gi� presente
			else if(impOnt.regionMatches(0, SubString, 0, SubString.length()) && !impOnt.equals(SubString))
				return posMatch;
			//Se una stringa gi� presente � sottostringa della stringa SubString
			else if(SubString.regionMatches(0, impOnt, 0, impOnt.length()) && !SubString.equals(impOnt))
				return -1;
			
			posMatch++;
		}
		
		return StringSet.size();
	}

}
