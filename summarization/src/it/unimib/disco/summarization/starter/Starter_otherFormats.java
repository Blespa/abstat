package it.unimib.disco.summarization.starter;

import it.unimib.disco.summarization.datatype.Concept;
import it.unimib.disco.summarization.datatype.EquConcept;
import it.unimib.disco.summarization.datatype.SubClassOf;
import it.unimib.disco.summarization.extraction.ConceptExtractor;
import it.unimib.disco.summarization.extraction.EqConceptExtractor;
import it.unimib.disco.summarization.info.InfoExtractor;
import it.unimib.disco.summarization.relation.OntologySubclassOfExtractor;
import it.unimib.disco.summarization.utility.ComputeLongestPathHierarchy;
import it.unimib.disco.summarization.utility.FileDataSupport;
import it.unimib.disco.summarization.utility.Model;

import java.io.File;



import com.hp.hpl.jena.n3.turtle.TurtleParseException;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.shared.JenaException;
import com.hp.hpl.jena.shared.NotFoundException;
import com.hp.hpl.jena.util.FileManager;



/**
 * Open an Ontology and extract all the useful information
 *
 * @author Vincenzo Ferme
 */
public class Starter_otherFormats {

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		// Load Ontology file
		// TODO: S - Read from directory or receive as command line input and Call extractor based on file type
		
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
		File folder = new File(owlBaseFileArg);
		File[] listOfFiles = folder.listFiles();
		String fileName = null;

		for (File file : listOfFiles) {
		    if (file.isFile()) {
		    	fileName = file.getName();
		  
			    String extension = "";
	
			    int i = fileName.lastIndexOf('.');
			    int p = Math.max(fileName.lastIndexOf('/'), fileName.lastIndexOf('\\'));
	
			    if (i > p) {
			        extension = fileName.substring(i+1);
			        if(extension=="owl"||extension=="nt") //Ho trovato il file di mio interesse
			        	break;
			    }
		    }
		}
		
		if(fileName==null){ //L'ontologia non è presente, o non è nel formato corretto
			
			System.out.println("Ontology not found!");
			System.exit(1);
			
		}
		
		//Base File
		String owlBaseFile = "File:" + owlBaseFileArg + fileName;
		//String owlBaseFile = "File:Ontology/university.owl";

		//Model
		/*OntModel ontologyModel = ModelFactory.createOntologyModel();
		Model myRawModel = FileManager.get().loadModel(owlBaseFile);
		ontologyModel.add(myRawModel);
*/
		OntModel ontologyModel;
		//try {
			
		Model OntModel = new Model(null,owlBaseFile,"TURTLE");
		ontologyModel = OntModel.getOntologyModel();
		
		/*} catch (TurtleParseException e) {
			ontologyModel = ModelFactory.createOntologyModel();
			System.out.println(e);
			
		} */

		
		 //Extract Concept from Ontology Model
		ConceptExtractor cExtract = new ConceptExtractor();
		cExtract.setConcepts(ontologyModel);
		
		Concept Concepts = new Concept();
		Concepts.setConcepts(cExtract.getConcepts());
		Concepts.setExtractedConcepts(cExtract.getExtractedConcepts());
		Concepts.setObtainedBy(cExtract.getObtainedBy());
		

		//Extract SubClassOf Relation from OntologyModel
		OntologySubclassOfExtractor SbExtractor = new OntologySubclassOfExtractor();
		//The Set of Concepts will be Updated if Superclasses are not in It
		SbExtractor.setConceptsSubclassOf(Concepts, ontologyModel);
		SubClassOf SubClassOfRelation = SbExtractor.getConceptsSubclassOf();
			
				
		//Extract EquivalentClass from Ontology Model - Qui per considerare tutti i concetti
		EqConceptExtractor equConcepts = new EqConceptExtractor();
		equConcepts.setEquConcept(Concepts, ontologyModel);
		
		EquConcept equConcept = new EquConcept();
		equConcept.setExtractedEquConcept(equConcepts.getExtractedEquConcept());
		equConcept.setEquConcept(equConcepts.getEquConcept());

		
		//Extract Labels and Comments
		InfoExtractor info = new InfoExtractor();
		info.setConceptInfo(Concepts);

		
		//Save Data in an Excel Report - TODO: Salvare su database
		
		//Pulisco i concetti da eventuali null e Thing
		Concepts.deleteThing();
		
		//Pulisco le relazioni di sottoclasse di Thing
		SubClassOfRelation.deleteThing();
        
        //Salvo le informazioni utilizzate per il calcolo dei percorsi nella gerarchia
        FileDataSupport writeFileSupp = new FileDataSupport(SubClassOfRelation, datasetSupportFileDirectory + "SubclassOf.txt", datasetSupportFileDirectory + "Concepts.txt", datasetSupportFileDirectory + "EquConcepts.txt", datasetSupportFileDirectory + "EquProperties.txt", datasetSupportFileDirectory + "DR.txt", datasetSupportFileDirectory + "Properties.txt", datasetSupportFileDirectory + "DTProperties.txt");
        writeFileSupp.writeSubclass(equConcept);
        
        //Calcolo tutti i percorsi nella gerarchia
        ComputeLongestPathHierarchy pathHierarchy = new ComputeLongestPathHierarchy(Concepts,datasetSupportFileDirectory + "SubclassOf.txt");
        pathHierarchy.computeLonghestPathHierarchy(datasetSupportFileDirectory + "path.txt",datasetSupportFileDirectory + "allSubConcept.txt");
        
        writeFileSupp.writeConcept(Concepts);
        writeFileSupp.writeEquclass(equConcept);

		
	}
	

}
