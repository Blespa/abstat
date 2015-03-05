package it.unimib.disco.summarization.utility;

import it.unimib.disco.summarization.datatype.Concept;
import it.unimib.disco.summarization.datatype.DomainRange;
import it.unimib.disco.summarization.datatype.EquConcept;
import it.unimib.disco.summarization.datatype.EquProperty;
import it.unimib.disco.summarization.datatype.Property;
import it.unimib.disco.summarization.datatype.SubClassOf;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.OntResource;

public class FileDataSupport {
	
	private SubClassOf subClassOfRelation;
	private String SubClFile;
	private String ConcFile;
	private String EquFile;
	private String EquPropFile;
	private String DRFile;
	private String PropFile;
	private String PropDTFile;

	public FileDataSupport(SubClassOf subClassOfRelation, String SubClFile, String ConcFile, String EquFile, String EquPropFile, String DRFile, String PropFile, String PropDTFile) {
		this.subClassOfRelation = subClassOfRelation;
		this.SubClFile = SubClFile;
		this.ConcFile = ConcFile;
		this.EquFile = EquFile;
		this.EquPropFile = EquPropFile;
		this.DRFile = DRFile;
		this.PropFile = PropFile;
		this.PropDTFile = PropDTFile;
	}
	
	
	public void writeSubclass(EquConcept equConcept){

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
					//TODO: Scrivere tutte le combinazioni nel caso sia Subj che Obj abbiano pi� di un equivalent class
					
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
			//Close the output stream
			out.close();
			
		}catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}
	
	public void writeConcept(Concept AllConcepts){

		Iterator<String> cIter = AllConcepts.getConcepts().keySet().iterator();
		
		try{
			// Create file 
			FileWriter fstream = new FileWriter(ConcFile);
			BufferedWriter out = new BufferedWriter(fstream);
			
			
			while (cIter.hasNext()) {
				String key = cIter.next().toString();
				
				out.write(key+"\n");
			}

			//Close the output stream
			out.close();
			
		}catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}
	
	public void writeProperty(Property AllProperty){

		List<OntProperty> extractedProp = AllProperty.getExtractedProperty();
		Iterator<OntProperty> ePropIt = extractedProp.iterator();


		try{
			// Create file 
			FileWriter fstream = new FileWriter(PropFile);
			BufferedWriter out = new BufferedWriter(fstream);
			
			FileWriter fstreamDT = new FileWriter(PropDTFile);
			BufferedWriter outDT = new BufferedWriter(fstreamDT);
			
			
			while (ePropIt.hasNext()) {
				OntProperty prop = ePropIt.next();
				
				if(prop.getRDFType(true).getLocalName().equals("DatatypeProperty")) //Se ancora non � conteggiata
				{
					outDT.write(prop.getURI()+"\n");
				}
				else
					out.write(prop.getURI()+"\n");
			}

			//Close the output stream
			out.close();
			outDT.close();
			
		}catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}
	
	public void writeEquclass(EquConcept equConcepts){

		Iterator<OntResource> pIter = equConcepts.getExtractedEquConcept().keySet().iterator();


		try{
			// Create file 
			FileWriter fstream = new FileWriter(EquFile);
			BufferedWriter out = new BufferedWriter(fstream);
			
			while (pIter.hasNext()) {
				OntResource key = pIter.next();
				List<OntResource> value = equConcepts.getExtractedEquConcept().get(key);
				
				if( value.size()>0 ) //Il dato concetto ha concetti equivalenti
				{
					out.write(key.getURI()); //Concetto
					
					for( OntResource subP : value){
						
						out.write("##"+subP.getURI()); //Concetto
					}
					
					out.write("\n");
				}
			}

			//Close the output stream
			out.close();
			
		}catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}
	
	public void writeEquProperty(EquProperty equProperties){

		Iterator<OntProperty> pIter = equProperties.getExtractedEquProperty().keySet().iterator();

		try{
			// Create file 
			FileWriter fstream = new FileWriter(EquPropFile);
			BufferedWriter out = new BufferedWriter(fstream);
			
			while (pIter.hasNext()) {
				OntResource key = pIter.next();
				List<OntProperty> value = equProperties.getExtractedEquProperty().get(key);
				
				if( value.size()>0 ) //La data propriet� ha propriet� equivalenti
				{
					out.write(key.getURI()); //Propriet�
					
					for( OntResource subP : value){
						
						out.write("##"+subP.getURI()); //Propriet�
					}
					
					out.write("\n");
				}
			}

			//Close the output stream
			out.close();
			
		}catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}
	
	public void writeDR(DomainRange DRRelation){
		
		//Scrivo le informazioni di DR su due file a seconda del tipo della risorsa presente come oggetto (Literal o Concetto)
		HashMap<String, ArrayList<OntResource>> DRRel = DRRelation.getDRRelation();
		Iterator<String> iterator = DRRel.keySet().iterator();
		
		try{
			// Create file 
			FileWriter fstream = new FileWriter(DRFile);
			BufferedWriter out = new BufferedWriter(fstream);
			
			while (iterator.hasNext()) {
				String key = iterator.next().toString();
				ArrayList<OntResource> value = DRRel.get(key);

				out.write(key + "##" + value.get(0).getURI() + "##" + value.get(1).getURI()); //Propriet�
				out.write("\n");
			}
			
			//Close the output stream
			out.close();
			
		}catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
		
	}
	
	private ArrayList<String> listRelativeEquConcept(EquConcept equConcept,OntClass equClass){
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
