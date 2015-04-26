package it.unimib.disco.summarization.extraction;

import it.unimib.disco.summarization.datatype.Properties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

/**
 * ConceptExtractor: Extract SubProperty Defined inside Ontology
 */
public class SubPropertyExtractor {
	
	private HashMap<OntProperty,List<OntProperty>> ExtractedSubProperty = new HashMap<OntProperty,List<OntProperty>>();
	private HashMap<String,HashMap<String,Integer>> Counter = new HashMap<String,HashMap<String,Integer>>(); //Property -> Context(Relation Type), Total


	public void setSubProperty(Properties properties) {

		Iterator<OntProperty> itP = properties.getExtractedProperty().iterator();

		while(itP.hasNext()) {
			OntProperty property = itP.next();
			//Get SubProperty from Model
			ExtendedIterator<? extends OntProperty> itSP = property.listSubProperties();
			
			List<OntProperty> subProp = new ArrayList<OntProperty>();
			
			while(itSP.hasNext()) {
				OntProperty cls11 = itSP.next();
				String URI = property.getURI();

				if( URI!=null ){
					subProp.add(cls11);

					//Count subproperty presence
					updateCounter(URI, "SubProperty");
				}

			}
			
			ExtractedSubProperty.put(property, subProp);
		}

	}
	
	public  HashMap<OntProperty,List<OntProperty>> getExtractedSubProperty() {
		return ExtractedSubProperty;
	}
	
	public void updateCounter(String URI, String Context){
		//Se la propriet� non � presente l'aggiungo
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
		//Se sia propriet� che contesto sono presenti aggiorno il contatore
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
