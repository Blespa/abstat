package it.unimib.disco.summarization.extraction;

import it.unimib.disco.summarization.datatype.Property;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.OWL;

/**
 * ConceptExtractor: Extract EquivalentProperty Defined inside Ontology
 */
public class EqPropertyExtractor {
	
	private HashMap<OntProperty,List<OntProperty>> ExtractedEquProperty = new HashMap<OntProperty,List<OntProperty>>();
	private HashMap<String,HashMap<String,Integer>> Counter = new HashMap<String,HashMap<String,Integer>>(); //Property -> Context(Relation Type), Total


	public void setEquProperty(Property properties, OntModel ontologyModel) {

		Iterator<OntProperty> itP = properties.getExtractedProperty().iterator();

		while(itP.hasNext()) {
			OntProperty property = itP.next();
			
			List<OntProperty> equProp = new ArrayList<OntProperty>();
			
			//SPARQL Query for EquProperty
			String queryString = "PREFIX ont:<" + property.getNameSpace() + ">" +
								 "PREFIX owl:<" + OWL.getURI() + ">" + 
								 "SELECT ?obj " +
								 "WHERE {" +
								 "      ont:" + property.getLocalName() + " owl:equivalentProperty ?obj" +
								 "      }";
			
			//Execute Query
			Query query = QueryFactory.create(queryString) ;
			QueryExecution qexec = QueryExecutionFactory.create(query, ontologyModel) ;
			
			try {
			    
				ResultSet results = qexec.execSelect();
			    
			    //Temporary Model in Order to Construct Node for External Concept
			    OntModel ontologyTempModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM, null); 
			    
			    //Extract EquProperty Relation
			    for ( ; results.hasNext() ; )
			    {
			      QuerySolution soln = results.nextSolution() ;

			      Resource obj = soln.getResource("obj");
			      String URIObj = obj.getURI();
			      
			      //Get EquProperty all Property different from the current one
				  if( URIObj!=null && property.getURI()!=URIObj ){
					  
					  OntProperty EquProp = ontologyTempModel.createOntProperty(URIObj);
					  
					  //Save EquProperty
					  if( URIObj!=null ){
						equProp.add(EquProp);

						//Count EquProperty presence
						updateCounter(property.getURI(), "EquivProperty");
					}

					}
					
					ExtractedEquProperty.put(property, equProp);
				  }


			} finally { qexec.close() ; }
			
		}

	}
	
	public  HashMap<OntProperty,List<OntProperty>> getExtractedEquProperty() {
		return ExtractedEquProperty;
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
