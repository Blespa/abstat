package it.unimib.disco.summarization.datatype;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntResource;

/**
 *  Provides datatype for store Concepts infos
 */
public class Concept {
	
	private HashMap<String,String> Concepts;
	private HashMap<String,String> ObtainedBy = new HashMap<String,String>();
	private HashMap<String,HashMap<String,Integer>> Counter = new HashMap<String,HashMap<String,Integer>>(); //Concept -> Context(Relation Type), Total
	private List<OntResource> ExtractedConcepts;

	public HashMap<String,String> getConcepts() {
		return Concepts;
	}

	public void setConcepts(HashMap<String,String> concepts) {
		Concepts = concepts;
	}

	public List<OntResource> getExtractedConcepts() {
		return ExtractedConcepts;
	}

	public void setExtractedConcepts(List<OntClass> extractedConceptsClass) {
		ExtractedConcepts = new ArrayList<OntResource>();
		//Cast to SuperClass in order to manipulate
		for(OntResource Class : extractedConceptsClass){
			ExtractedConcepts.add((OntResource) Class);
			//Count direct presence
			HashMap<String,Integer> count = new HashMap<String,Integer>();
			count.put("Direct",1);
			getCounter().put(Class.getURI(),count);
		}
	}
	
	//Pulisco i concetti da eventuali null e Thing
	public void deleteThing(){
		Iterator<String> cIter = Concepts.keySet().iterator();
		while (cIter.hasNext()) {
			String key = cIter.next().toString();
			String value = Concepts.get(key).toString();

			if(value.equals("null") || value.equals("Thing") || key.equals("null") || key.equals("Thing")){
				getCounter().remove(getCounter().get(key));
				cIter.remove();
			}
		}
		
		Iterator<OntResource> ScIter = ExtractedConcepts.iterator();

		while (ScIter.hasNext()) {
			OntResource curEl = ScIter.next();
			String localNameSup = curEl.getLocalName();

			if(localNameSup.equals("Thing")){
				getCounter().remove(curEl.getURI());
				ScIter.remove();
			}
		}
	}

	public HashMap<String,String> getObtainedBy() {
		return ObtainedBy;
	}

	public void setNewObtainedBy(String URI, String obtainedBy) {
		getObtainedBy().put(URI, obtainedBy);
	}

	public void setObtainedBy(HashMap<String,String> obtainedBy) {
		ObtainedBy = obtainedBy;
	}
	
	public void updateCounter(String URI, String Context){
		//Se il concetto non � presente l'aggiungo
		if(Counter.get(URI)==null){
			//Count direct presence
			HashMap<String,Integer> count = new HashMap<String,Integer>();
			count.put(Context, new Integer(1));
			Counter.put(URI, count);
		}
		//Se il contesto non � presente l'aggiungo
		else if(Counter.get(URI).get(Context)==null){
			Counter.get(URI).put(Context, new Integer(1));
		}
		//Se sia concetto che contesto sono presenti aggiorno il contatore
		else{
			Counter.get(URI).put(Context, new Integer(Integer.valueOf(Counter.get(URI).get(Context)) + 1));
		}
	}
	public HashMap<String,HashMap<String,Integer>> getCounter() {
		return Counter;
	}

	public void setCounter(HashMap<String,HashMap<String,Integer>> counter) {
		Counter = counter;
	}		

}
