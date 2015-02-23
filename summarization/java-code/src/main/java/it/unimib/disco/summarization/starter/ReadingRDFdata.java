package it.unimib.disco.summarization.starter;

import it.unimib.disco.summarization.datatype.Concept;
import it.unimib.disco.summarization.datatype.EquConcept;
import it.unimib.disco.summarization.datatype.SubClassOf;
import it.unimib.disco.summarization.extraction.ConceptExtractor;
import it.unimib.disco.summarization.extraction.EqConceptExtractor;
import it.unimib.disco.summarization.relation.OntologySubclassOfExtractor;
import it.unimib.disco.summarization.utility.ComputeLongestPathHierarchy;
import it.unimib.disco.summarization.utility.FileDataSupport;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDFS;

public class ReadingRDFdata{
	public static void main (String args[]){
		//Parametri
		String owlBaseFileArg = null;
		String reportDirectory = null;
		String datasetSupportFileDirectory = null;
				
		if (args.length == 3) {
					
			owlBaseFileArg=args[0];
			reportDirectory=args[1];
			datasetSupportFileDirectory=args[2];
					
		}
		else{
			System.err.println("Argument" + " must be 3 in this order: Ontology File Directory, Repor Directory (with / at the and), Dataset Computation Support Files Directory (with / at the and)");
			System.exit(1);						
		}

		//ONTOLOGY
				
		//Ottengo il nome del file che rappresenta l'ontologia, con path assoluto per poter caricare il Model di Jena
		File RDF_FILE = new File("/Users/anisarula/Documents/ontology/yagoSimpleTaxonomy.ttl"); 
				
		if(RDF_FILE.getName()==null){ //L'ontologia non è presente, o non è nel formato corretto
					
			System.out.println("Ontology not found!");
			System.exit(1);
					
		}	
		Model tmpModel = ModelFactory.createDefaultModel();
		FileManager.get().readModel(tmpModel, "http://eis-bonn.github.io/Luzzu/ontologies/daq/");
		//FileManager.get().readModel(tmpModel,RDF_FILE.getPath(),"TURTLE");
	    System.out.println("Loading: " + tmpModel.size() + " triple...\n");
	       
	    OntModel m = ModelFactory.createOntologyModel();
	    m.add(tmpModel);
	    
	    //Extract Concept from Ontology Model
	  		ConceptExtractor cExtract = new ConceptExtractor();
	  		cExtract.setConcepts(m);
	  		
	  		Concept Concepts = new Concept();
	  		Concepts.setConcepts(cExtract.getConcepts());
	  		Concepts.setExtractedConcepts(cExtract.getExtractedConcepts());
	  		Concepts.setObtainedBy(cExtract.getObtainedBy());

	  		//Extract SubClassOf Relation from OntologyModel
	  		OntologySubclassOfExtractor SbExtractor = new OntologySubclassOfExtractor();
			//The Set of Concepts will be Updated if Superclasses are not in It
			SbExtractor.setConceptsSubclassOf(Concepts, m);
			SubClassOf SubClassOfRelation = SbExtractor.getConceptsSubclassOf();
			
			// retrieve the value of the N property
			/*List<Resource> ExtractedConcepts = new ArrayList<Resource>();
			ResIterator iter = m.listSubjectsWithProperty(RDFS.subClassOf);
			while (iter.hasNext()) {
			    Resource r = iter.nextResource();
			    ExtractedConcepts.add(r);
			    //System.out.println("  " + r.toString());
			}
			 */
				
					
			//Extract EquivalentClass from Ontology Model - Qui per considerare tutti i concetti
			/*ResIterator itereq = m.listResourcesWithProperty(OWL.equivalentClass);
			while (itereq.hasNext()) {
			    Resource r = itereq.nextResource();
			    //System.out.println("  " + r.toString());
			}*/
			
			//Extract EquivalentClass from Ontology Model - Qui per considerare tutti i concetti
			EqConceptExtractor equConcepts = new EqConceptExtractor();
			equConcepts.setEquConcept(Concepts, m);
			
			EquConcept equConcept = new EquConcept();
			equConcept.setExtractedEquConcept(equConcepts.getExtractedEquConcept());
			equConcept.setEquConcept(equConcepts.getEquConcept());
			

			
			//Save Data in an Excel Report - TODO: Salvare su database
			
			//Pulisco i concetti da eventuali null e Thing
			Concepts.deleteThing();
			
			//Pulisco le relazioni di sottoclasse di Thing
			//SubClassOfRelation.deleteThing();
	        
	        //Salvo le informazioni utilizzate per il calcolo dei percorsi nella gerarchia
	     FileDataSupport writeFileSupp = new FileDataSupport(SubClassOfRelation, datasetSupportFileDirectory + "SubclassOf.txt", datasetSupportFileDirectory + "Concepts.txt", datasetSupportFileDirectory + "EquConcepts.txt", datasetSupportFileDirectory + "EquProperties.txt", datasetSupportFileDirectory + "DR.txt", datasetSupportFileDirectory + "Properties.txt", datasetSupportFileDirectory + "DTProperties.txt");
	      writeFileSupp.writeSubclass(equConcept);
	        
	        //Calcolo tutti i percorsi nella gerarchia
	        ComputeLongestPathHierarchy pathHierarchy = new ComputeLongestPathHierarchy(Concepts,datasetSupportFileDirectory + "SubclassOf.txt");
	        pathHierarchy.computeLonghestPathHierarchy(datasetSupportFileDirectory + "path.txt",datasetSupportFileDirectory + "allSubConcept.txt");
	        
	      writeFileSupp.writeConcept(Concepts);
	}
}