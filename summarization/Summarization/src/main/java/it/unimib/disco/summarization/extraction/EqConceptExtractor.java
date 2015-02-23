package it.unimib.disco.summarization.extraction;

import it.unimib.disco.summarization.datatype.Concept;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntResource;
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
public class EqConceptExtractor {
	
	private HashMap<OntResource,List<OntResource>> ExtractedEquConcept = new HashMap<OntResource,List<OntResource>>();
	private ArrayList<String> equConcept = new ArrayList<String>();

	public void setEquConcept(Concept AllConcepts, OntModel ontologyModel) {

		Iterator<OntResource> itC = AllConcepts.getExtractedConcepts().iterator();

		while(itC.hasNext()) {
			OntResource concept = itC.next();
			
			List<OntResource> equConc = new ArrayList<OntResource>();
			
			//SPARQL Query for EquProperty
			String queryString = "PREFIX ont:<" + concept.getNameSpace() + ">" +
								 "PREFIX owl:<" + OWL.getURI() + ">" + 
								 "SELECT ?obj " +
								 "WHERE {" +
								 "      ont:" + concept.getLocalName() + " owl:equivalentClass ?obj" +
								 "      }";
			
			//Execute Query
			Query query = QueryFactory.create(queryString) ;
			QueryExecution qexec = QueryExecutionFactory.create(query, ontologyModel) ;
			
			try {
			    
				ResultSet results = qexec.execSelect();
			    
			    //Temporary Model in Order to Construct Node for External Concept
			    OntModel ontologyTempModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM, null); 
			    
			    //Extract EquConcept Relation
			    for ( ; results.hasNext() ; )
			    {
			      QuerySolution soln = results.nextSolution() ;

			      Resource obj = soln.getResource("obj");
			      String URIObj = obj.getURI();
			      
			      //Get EquClass all Concept different from the current one
				  if( URIObj!=null && concept.getURI()!=URIObj ){
					  
					  OntResource EquConc = ontologyTempModel.createOntResource(URIObj);
					  
					  //Save EquC
					  if( URIObj!=null ){
						equConc.add(EquConc);
						
						equConcept.add(EquConc.getURI());
						
						//Count Presence of Class as EquConcept
						AllConcepts.updateCounter(concept.getURI(), "Equivalent Class");
					}

					}
					
					ExtractedEquConcept.put(concept, equConc);
				  }


			} finally { qexec.close() ; }
			
		}

	}
	
	public  HashMap<OntResource, List<OntResource>> getExtractedEquConcept() {
		return ExtractedEquConcept;
	}

	public ArrayList<String> getEquConcept() {
		return equConcept;
	}

	public void setEquConcept(ArrayList<String> equConcept) {
		this.equConcept = equConcept;
	}
	
}
