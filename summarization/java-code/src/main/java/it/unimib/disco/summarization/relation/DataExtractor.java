/**
 * 
 */
package it.unimib.disco.summarization.relation;

import it.unimib.disco.summarization.datatype.Concept;
import it.unimib.disco.summarization.datatype.EquConcept;
import it.unimib.disco.summarization.datatype.EquProperty;
import it.unimib.disco.summarization.datatype.Row;
import it.unimib.disco.summarization.datatype.SubClassOf;
import it.unimib.disco.summarization.utility.RDFLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.hp.hpl.jena.ontology.ConversionException;
import com.hp.hpl.jena.ontology.OntClass;
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
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.util.iterator.Filter;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

/**
 * The Class DataExtractor.
 * 
 * OBSOLETO: Far riferimento agli script shell e awk
 *
 * @author Vincenzo Ferme
 */
public class DataExtractor {
	
	//Salva concetti minimi(Identificativo: 1) e massimi(Identificativo: 2) della gerarchia, oltre che i concetti non presenti(Identificativo: 0) nella gerarchia dell'ontologia
	private HashMap<String, ArrayList<Row>> resourceType = new HashMap<String,ArrayList<Row>>(); //URI -> {URIs,Identificativo}
	
	public void extractInfoFromData(Concept Concepts, EquProperty equProperties, EquConcept equConcept, RDFLoader sd, int numberOfFiles, OntModel ontologyModel){
		
		List<OntResource> AddConcepts = new ArrayList<OntResource>();
		
		for(int file=0; file<numberOfFiles; file++){ //Scorro tutti i file contenenti informazioni
        	com.hp.hpl.jena.rdf.model.Model tripleModel = sd.loadRDFtoModel(file);
        	
        	//Estraggo le informazioni dai dati
        	StmtIterator triple = tripleModel.listStatements();
        	
        	int execCounter = 0;
        	
        	while(triple.hasNext()){
        		Statement statement = triple.next();

        		if(statement.getPredicate().equals(RDF.type)){ //Get Rdf Type
        			
        			//System.out.println("CONCEPT CHECK FOR " + statement.getSubject().toString());
        			
        			if(execCounter%10==0){
        				System.out.println("Current: " + execCounter + "...");
        			}
        			
        			execCounter++;
        			
        			//Se non � nell'elenco dei concetti o delle equivalentClass
            		if(!statement.getObject().toString().contains("Thing") 
            				&& Concepts.getConcepts().get(statement.getObject().toString())==null 
            				&& findEquPropertybyURI(equConcept.getEquConcept(),statement.getObject())==null ){
            			
            			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> ADD: " + statement.getObject() + "\n");
            			
            			Concepts.getConcepts().put(statement.getObject().toString(),localName(((Resource) statement.getObject()).getURI(),((Resource) statement.getObject()).getNameSpace()));
						Concepts.setNewObtainedBy(statement.getObject().toString(), "Data-Type - " + localName(((Resource) statement.getSubject()).getURI(),((Resource) statement.getSubject()).getNameSpace()));
						AddConcepts.add(ontologyModel.createOntResource(statement.getObject().toString()));

            		}
            		
            		//Salvo il tipo della risorsa se � minimo, massimo o sconosciuto
            		setMinMaxType(resourceType, Concepts, equConcept, ontologyModel, statement);
            		
        		}
        	}
        }
		
		Concepts.getExtractedConcepts().addAll(AddConcepts);
		
	}
	
	//Setta il tipo massimo, minimo e gli sconosciuti nella gerarchia di concetti. Contestualmente conta il numero di istanze per ogni minimo della gerarchia
	private void setMinMaxType(HashMap<String, ArrayList<Row>> resourceType, Concept Concepts, EquConcept equConcept, OntModel ontologyModel, Statement statement){
				
		OntClass concOnt = ontologyModel.getOntClass(statement.getObject().toString());	

		if( concOnt==null ){ //L'ordine della gerarchia � sconosciuto

			//Risolvo il tipo con le equivalent Class, se possibile e necessario
			RDFNode typeSolved = solveEquivalentClass(equConcept,statement.getObject());

			if(typeSolved!=null)
			{
				OntClass concOntEq = ontologyModel.getOntClass(typeSolved.toString());	
				concOnt = concOntEq;
			}
		}

		if( concOnt==null ){ //L'ordine della gerarchia � sconosciuto
			//Salvo le info
			if(resourceType.get(statement.getSubject().toString())==null)
			{
				//E' il primo tipo indicato per questa risorsa
    			ArrayList<Row> type = new ArrayList<Row>();
    			
    			type.add(new Row("3",statement.getObject().toString().intern()));
    			
    			resourceType.put(statement.getSubject().toString(), type);
			}
			else{
				
				//Aggiungo il nuovo tipo all'elenco dei tipi, se non � gi� presente
				ArrayList<Row> type = resourceType.get(statement.getSubject().toString());
				
				boolean exist = false;
				
				for(int i=0;i<type.size();i++){
					if(type.get(i).equals(statement.getObject().toString())){
						exist=true;
						break;
					}
				}

				if( exist==false ){
					type.add(new Row("3",statement.getObject().toString().intern()));
	    			
	    			resourceType.put(statement.getSubject().toString(), type);
				}
			}

			//System.out.println("ADD UNKN TYPE FOR: " + localName(res.getURI(),res.getNameSpace()) + "(" + res.getURI() + ") - " + type.toString() );
		}
		else{
			//System.out.println("CONCETTO DA MODELLO: " + concOnt.getURI());

			//Get List Of All Directed SuperClasses
			//System.out.println("RISORSA: " + res.getLocalName() + " TIPO:" + concOnt.getLocalName());

			Iterator<OntClass> itSup;

			try{
				itSup = concOnt.listSuperClasses(true).filterDrop(new Filter<OntClass>() {
					public boolean accept( OntClass o ) {
						return localName(o.asClass().getURI(),o.asClass().getNameSpace()).equals("Thing");
					}});
			}
			catch(ConversionException e){ //if Concept id Defined Outside Ontology
				//SPARQL Query for SuperClasses
				String queryString = "PREFIX rdfs:<" + RDFS.getURI() + "> " +
						"SELECT ?obj " +
						"WHERE {" +
						"       <" +  concOnt.getURI() + "> rdfs:subClassOf ?obj" +
						"      }";

				ArrayList<OntClass> superClasses = new ArrayList<OntClass>(); 

				//Execute Query
				Query query = QueryFactory.create(queryString) ;
				QueryExecution qexec = QueryExecutionFactory.create(query, ontologyModel) ;

				try {

					ResultSet results = qexec.execSelect();

					//Temporary Model in Order to Construct Node for External Concept
					OntModel ontologyTempModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM, null); 

					//Extract SubCallOf Relation
					for ( ; results.hasNext() ; )
					{
						QuerySolution soln = results.nextSolution() ;

						Resource obj = soln.getResource("obj");
						String URIObj = obj.getURI();

						//Get SubClassOf all Class different from the current one and Thing
						if( URIObj!=null && concOnt.getURI()!=URIObj && !localName(obj.getURI(),obj.getNameSpace()).equals("Thing") ){

							OntClass conceptSup = ontologyTempModel.createClass(URIObj);

							superClasses.add(conceptSup);

							//System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>ADD SPARQL: " + conceptSup.getURI());
						}

					}

					itSup = superClasses.iterator();

				} finally { qexec.close() ; }
			}

			//Get List Of All Directed SubClasses
			Iterator<OntClass> itSub;

			//System.out.println("CONCETTO DA MODELLO: " + concOnt.getURI());

			//System.out.println("RISORSA: " + res.getLocalName() + " TIPO:" + concOnt.getLocalName());

			try{
				itSub = concOnt.listSubClasses(true).filterDrop(new Filter<OntClass>() {
					public boolean accept( OntClass o ) {
						return localName(o.asClass().getURI(),o.asClass().getNameSpace()).equals("Thing");
					}});
			}
			catch(ConversionException e){ //if Concept id Defined Outside Ontology
				//SPARQL Query for SubClasses
				String queryString = "PREFIX rdfs:<" + RDFS.getURI() + "> " +
						"SELECT ?subj " +
						"WHERE {" +
						"      ?subj rdfs:subClassOf " + "<" +  concOnt.getURI() + ">" + 
						"      }";

				ArrayList<OntClass> subClasses = new ArrayList<OntClass>(); 

				//Execute Query
				Query query = QueryFactory.create(queryString) ;
				QueryExecution qexec = QueryExecutionFactory.create(query, ontologyModel) ;

				try {

					ResultSet results = qexec.execSelect();

					//Temporary Model in Order to Construct Node for External Concept
					OntModel ontologyTempModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM, null); 

					//Extract SubCallOf Relation
					for ( ; results.hasNext() ; )
					{
						QuerySolution soln = results.nextSolution() ;

						Resource subj = soln.getResource("subj");
						String URISubj = subj.getURI();

						//Get SubClassOf all Class different from the current one and Thing
						if( URISubj!=null && concOnt.getURI()!=URISubj && !localName(subj.getURI(),subj.getNameSpace()).equals("Thing") ){

							OntClass conceptSub = ontologyTempModel.createClass(URISubj);

							subClasses.add(conceptSub);

							//System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>ADD SPARQL: " + conceptSup.getURI());
						}

					}

					itSub = subClasses.iterator();

				} finally { qexec.close() ; }
			}


			if(!itSup.hasNext() && !localName(concOnt.asClass().getURI(),concOnt.asClass().getNameSpace()).equals("Thing")){ //Non ha Superclassi e non � Thing
				//Salvo le info
				if(resourceType.get(statement.getSubject().toString())==null)
				{
					//E' il primo tipo indicato per questa risorsa
	    			ArrayList<Row> type = new ArrayList<Row>();
	    			
	    			type.add(new Row("2",statement.getObject().toString().intern()));
	    			
	    			resourceType.put(statement.getSubject().toString(), type);
				}
				else{
					
					//Aggiungo il nuovo tipo all'elenco dei tipi, se non � gi� presente
					ArrayList<Row> type = resourceType.get(statement.getSubject().toString());
					
					boolean exist = false;
					
					for(int i=0;i<type.size();i++){
						if(type.get(i).equals(statement.getObject().toString())){
							exist=true;
							break;
						}
					}

					if( exist==false ){
						type.add(new Row("2",statement.getObject().toString().intern()));
		    			
		    			resourceType.put(statement.getSubject().toString(), type);
					}
				}

				//System.out.println("ADD TOP HIER TYPE FOR: " + localName(res.getURI(),res.getNameSpace()) + "(" + res.getURI() + ") - " + localName(((Resource) type).getURI(),((Resource) type).getNameSpace()) );
			}
			else if(!itSub.hasNext() && !localName(concOnt.asClass().getURI(),concOnt.asClass().getNameSpace()).equals("Thing")){ //Non ha SottoClassi e non � Thing

				//Salvo le info
				if(resourceType.get(statement.getSubject().toString())==null)
				{
					//E' il primo tipo indicato per questa risorsa
	    			ArrayList<Row> type = new ArrayList<Row>();
	    			
	    			type.add(new Row("1",statement.getObject().toString().intern()));
	    			
	    			resourceType.put(statement.getSubject().toString(), type);
				}
				else{
					
					//Aggiungo il nuovo tipo all'elenco dei tipi, se non � gi� presente
					ArrayList<Row> type = resourceType.get(statement.getSubject().toString());
					
					boolean exist = false;
					
					for(int i=0;i<type.size();i++){
						if(type.get(i).equals(statement.getObject().toString())){
							exist=true;
							break;
						}
					}

					if( exist==false ){
						type.add(new Row("1",statement.getObject().toString().intern()));
		    			
		    			resourceType.put(statement.getSubject().toString(), type);
					}
				}

				//Count Presence of a Instance for Min Type
				Concepts.updateCounter(statement.getObject().toString(), "Instances");

				//System.out.println("ADD BOTTOM HIER TYPE FOR: " + statement.getSubject().toString() + "- " + statement.getObject().toString() );

			}
		}
		
	}
	
	
	//Utilizzato per ottenere il local name ed ovviare ai problema di getLocalName di Jena
	private String localName(String URI, String NameSpace){
		return URI.replace(NameSpace, "");
	}
	
	//Utilizzato per risolvere l'utilizzo delle equivalent Class
	private RDFNode solveEquivalentClass(EquConcept equConcept, RDFNode type){
		
		RDFNode solvedType = null;
		
		Iterator<OntResource> itEq = equConcept.getExtractedEquConcept().keySet().iterator();
		
		while(itEq.hasNext()){
			OntResource Class = itEq.next();
			List<OntResource> equClass = equConcept.getExtractedEquConcept().get(Class);
			
			Iterator<OntResource> equClassIter = equClass.iterator();
			
			while(equClassIter.hasNext()){
				
				OntResource eqCl = equClassIter.next();
				
				if(eqCl.getURI().equals(type.toString())){ //Ho trovato la classe equivalente
					
					//System.out.println("EQUIV CLASS: " + Class.getURI() + " FOR: " + type.toString());
					
					solvedType = Class;
					
				}
			}
			
		}
		
		return solvedType;
		
	}
	
	private String findEquPropertybyURI(ArrayList<String> arrayList, RDFNode rdfNode){    
		for (String instance : arrayList) {
	    	
	        if (instance.equals(rdfNode.toString())) {
	        	//System.out.println("CHECK ARRAY RETURN: " + instance);
	            return instance;
	        }
	    }
	    return null; 
	}
}
