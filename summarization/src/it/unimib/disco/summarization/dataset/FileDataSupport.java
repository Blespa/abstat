package it.unimib.disco.summarization.dataset;

import it.unimib.disco.summarization.ontology.Concepts;
import it.unimib.disco.summarization.ontology.EquivalentConcepts;
import it.unimib.disco.summarization.ontology.SubClassOf;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntResource;

public class FileDataSupport {
	
	private SubClassOf subClassOfRelation;
	private String SubClFile;
	private String ConcFile;

	public FileDataSupport(SubClassOf subClassOfRelation, String SubClFile, String ConcFile) {
		this.subClassOfRelation = subClassOfRelation;
		this.SubClFile = SubClFile;
		this.ConcFile = ConcFile;
	}
	
	
	public void writeSubclass(EquivalentConcepts equConcept){

		Iterator<List<OntClass>> ScIter = subClassOfRelation.getConceptsSubclassOf().iterator();
		

		try{
			// Create file 
			FileWriter fstream = new FileWriter(SubClFile);
			BufferedWriter out = new BufferedWriter(fstream);

			while (ScIter.hasNext()) {
				List<OntClass> curEl = ScIter.next();
				
				ArrayList<String> equClassSubj = listRelativeEquConcept(equConcept,curEl.get(0));
				ArrayList<String> equClassObj = listRelativeEquConcept(equConcept,curEl.get(1));
				
				if(equClassSubj.size()>0 && equClassObj.size()>0){
					
					if(equClassSubj.size()==1){
						
						if(equClassObj.size()==1){
							
							Iterator<String> equClassSubjIter = equClassSubj.iterator();
							Iterator<String> equClassObjIter = equClassObj.iterator();
							
							while(equClassSubjIter.hasNext()){
								
								String equClassSubjURI = equClassSubjIter.next();
								String equClassObjURI = equClassObjIter.next();
								
								out.write(equClassSubjURI+"##"+equClassObjURI+"\n");
								
							}
							
						}
					}
				}
				else if(equClassSubj.size()>0){
					
					Iterator<String> equClassSubjIter = equClassSubj.iterator();

					while(equClassSubjIter.hasNext()){

						String equClassSubjURI = equClassSubjIter.next();

						out.write(equClassSubjURI+"##"+curEl.get(1).getURI()+"\n");

					}

				}
				else if(equClassObj.size()>0){
					
					Iterator<String> equClassObjIter = equClassObj.iterator();

					while(equClassObjIter.hasNext()){

						String equClassObjURI = equClassObjIter.next();

						out.write(curEl.get(0).getURI()+"##"+equClassObjURI+"\n");

					}
						
				}
				else{	
					out.write(curEl.get(0).getURI()+"##"+curEl.get(1).getURI()+"\n");
				}
				
			}
			out.close();
			
		}catch (Exception e){
			System.err.println("Error: " + e.getMessage());
		}
	}
	
	public void writeConcept(Concepts AllConcepts){

		Iterator<String> cIter = AllConcepts.getConcepts().keySet().iterator();
		
		try{
			// Create file 
			FileWriter fstream = new FileWriter(ConcFile);
			BufferedWriter out = new BufferedWriter(fstream);
			
			
			while (cIter.hasNext()) {
				String key = cIter.next().toString();
				
				out.write(key+"\n");
			}

			out.close();
			
		}catch (Exception e){
			System.err.println("Error: " + e.getMessage());
		}
	}
	
	private ArrayList<String> listRelativeEquConcept(EquivalentConcepts equConcept,OntClass equClass){
		HashMap<OntResource,List<OntResource>> ExtractedEquConcept = equConcept.getExtractedEquConcept();
		
		ArrayList<String> equConcepts = new ArrayList<String>();
		
		//Cerco la classe nell'elenco delle equivalent class
		Iterator<OntResource> pIter = ExtractedEquConcept.keySet().iterator();

		while (pIter.hasNext()) {
			OntResource key = pIter.next();
			List<OntResource> value = ExtractedEquConcept.get(key);
			
			if( value.size()>0 ) //Il dato concetto ha concetti equivalenti
			{

				for( OntResource subP : value){
					
					//Ho trovato un concetto equivalente
					if(subP.getURI().equals(equClass.getURI())){
						equConcepts.add(key.getURI());
					}
					
				}

			}
		}
		return equConcepts;
	}
}
