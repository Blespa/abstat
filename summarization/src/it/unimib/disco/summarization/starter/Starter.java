package it.unimib.disco.summarization.starter;

import it.unimib.disco.summarization.datatype.Axiom;
import it.unimib.disco.summarization.datatype.Concepts;
import it.unimib.disco.summarization.datatype.DomainRange;
import it.unimib.disco.summarization.datatype.EquivalentConcepts;
import it.unimib.disco.summarization.datatype.EquProperty;
import it.unimib.disco.summarization.datatype.InvProperty;
import it.unimib.disco.summarization.datatype.LiteralAxiom;
import it.unimib.disco.summarization.datatype.Properties;
import it.unimib.disco.summarization.datatype.SubClassOf;
import it.unimib.disco.summarization.datatype.SubProperty;
import it.unimib.disco.summarization.extraction.ConceptExtractor;
import it.unimib.disco.summarization.extraction.EqConceptExtractor;
import it.unimib.disco.summarization.extraction.EqPropertyExtractor;
import it.unimib.disco.summarization.extraction.InvPropertyExtractor;
import it.unimib.disco.summarization.extraction.PropertyExtractor;
import it.unimib.disco.summarization.extraction.SubPropertyExtractor;
import it.unimib.disco.summarization.info.InfoExtractor;
import it.unimib.disco.summarization.relation.OntologyAxiomExtractor;
import it.unimib.disco.summarization.relation.OntologyDomainRangeExtractor;
import it.unimib.disco.summarization.relation.OntologySubclassOfExtractor;
import it.unimib.disco.summarization.utility.CreateExcel;
import it.unimib.disco.summarization.utility.FileDataSupport;
import it.unimib.disco.summarization.utility.FileSystemConnector;
import it.unimib.disco.summarization.utility.InputFile;
import it.unimib.disco.summarization.utility.LongestPaths;
import it.unimib.disco.summarization.utility.Model;
import it.unimib.disco.summarization.utility.TextInput;
import it.unimib.disco.summarization.utility.UsedOntology;

import java.io.File;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.hp.hpl.jena.ontology.OntModel;


/**
 * Open an Ontology and extract all the useful information
 *
 * @author Vincenzo Ferme
 */
public class Starter {

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		new Events();
		
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
		Collection<File> listOfFiles = FileUtils.listFiles(folder, new String[]{"owl"}, false);
		String fileName = listOfFiles.iterator().next().getName();
		
		if(fileName==null){ //L'ontologia non � presente, o non � nel formato corretto
			
			System.out.println("Ontology not found!");
			System.exit(1);
			
		}
		
		//Base File
		String owlBaseFile = "file://" + owlBaseFileArg + "/" + fileName;
		//String owlBaseFile = "File:Ontology/university.owl";

		//Model
		Model OntModel = new Model(null,owlBaseFile,"RDF/XML");
		OntModel ontologyModel = OntModel.getOntologyModel();
		
		//Extract Property from Ontology Model
		PropertyExtractor pExtract = new PropertyExtractor();
		pExtract.setProperty(ontologyModel);
		
		Properties properties = new Properties();
		properties.setProperty(pExtract.getProperty());
		properties.setExtractedProperty(pExtract.getExtractedProperty());
		properties.setCounter(pExtract.getCounter());
		
		//Extract SubProperty from Ontology Model
		SubPropertyExtractor spExtract = new SubPropertyExtractor();
		spExtract.setSubProperty(properties);
		
		SubProperty subProperties = new SubProperty();
		subProperties.setExtractedSubProperty(spExtract.getExtractedSubProperty());
		subProperties.setCounter(spExtract.getCounter());
		
		//Extract InverseProperty from Ontology Model
		InvPropertyExtractor ipExtract = new InvPropertyExtractor();
		ipExtract.setInvProperty(properties);
		
		InvProperty invProperties = new InvProperty();
		invProperties.setExtractedInvProperty(ipExtract.getExtractedInvProperty());
		invProperties.setCounter(ipExtract.getCounter());
		
		//Extract EquivalentProperty from Ontology Model
		EqPropertyExtractor epExtract = new EqPropertyExtractor();
		epExtract.setEquProperty(properties, ontologyModel);
				
		EquProperty equProperties = new EquProperty();
		equProperties.setExtractedEquProperty(epExtract.getExtractedEquProperty());
		equProperties.setCounter(epExtract.getCounter());
		

		//Extract Concept from Ontology Model
		ConceptExtractor cExtract = new ConceptExtractor();
		cExtract.setConcepts(ontologyModel);
		
		Concepts concepts = new Concepts();
		concepts.setConcepts(cExtract.getConcepts());
		concepts.setExtractedConcepts(cExtract.getExtractedConcepts());
		concepts.setObtainedBy(cExtract.getObtainedBy());
		
		//Extract SubClassOf Relation from OntologyModel
		OntologySubclassOfExtractor SbExtractor = new OntologySubclassOfExtractor();
		//The Set of Concepts will be Updated if Superclasses are not in It
		SbExtractor.setConceptsSubclassOf(concepts, ontologyModel);
		SubClassOf SubClassOfRelation = SbExtractor.getConceptsSubclassOf();
		
		//Extract Domain & Range Relation
		OntologyDomainRangeExtractor DRExtractor = new OntologyDomainRangeExtractor();
		//The Set of Concepts will be Updated if Domain or Range are not in It
		DRExtractor.setConceptsDomainRange(concepts, properties);
		DomainRange DRRelation = DRExtractor.getPropertyDomainRange();
		
		//Extract Axioms from OntologyModel
		OntologyAxiomExtractor SVFExtractor = new OntologyAxiomExtractor();
		//The Set of Concepts will be Updated if Superclasses are not in It
		SVFExtractor.setConceptsSomeValueFrom(concepts, DRRelation, ontologyModel);
		SVFExtractor.setConceptsAllValueFrom(concepts, DRRelation, ontologyModel);
		SVFExtractor.setConceptsMinCardinality(concepts, DRRelation, ontologyModel);
		Axiom SomeValueFromRelation = SVFExtractor.getConceptsSomeValueFrom();
		Axiom AllValueFromRelation = SVFExtractor.getConceptsAllValueFrom();
		Axiom MinCardinalityRelation = SVFExtractor.getConceptsMinCardinality();
		LiteralAxiom MinCardinalityLiteralRelation = SVFExtractor.getConceptsMinCardinalityLiteral();
		LiteralAxiom SomeValueFromLiteralRelation = SVFExtractor.getConceptsSomeValueFromLiteral();
		LiteralAxiom AllValueFromLiteralRelation = SVFExtractor.getConceptsAllValueFromLiteral();
		
		//Extract EquivalentClass from Ontology Model - Qui per considerare tutti i concetti
		EqConceptExtractor equConcepts = new EqConceptExtractor();
		equConcepts.setEquConcept(concepts, ontologyModel);
		
		EquivalentConcepts equConcept = new EquivalentConcepts();
		equConcept.setExtractedEquConcept(equConcepts.getExtractedEquConcept());
		equConcept.setEquConcept(equConcepts.getEquConcept());
		
		//Get List of Used Ontology
		List<String> usedOntology = UsedOntology.getUsedOntology(ontologyModel, concepts, properties, equProperties);
		
		//Extract Labels and Comments
		InfoExtractor info = new InfoExtractor();
		info.setConceptInfo(concepts);
		info.setPropertyInfo(properties);
		
		//Save Data in an Excel Report - TODO: Salvare su database
		
		//Pulisco i concetti da eventuali null e Thing
		concepts.deleteThing();
		
		//Pulisco le relazioni di sottoclasse di Thing
		SubClassOfRelation.deleteThing();
		
		//Write and Excel Report
		CreateExcel report = new CreateExcel();
		//report.setOutputFile("OutputTest/University/report.xls");
		report.setOutputFile(reportDirectory + "/ontology.xls");
		
		//Write Down File
		
		try {
			//Create File to Write
			report.startWrite();
			
			//Write Data
			report.generateConceptsSheet(concepts,0);
			report.generateEquConceptsSheet(equConcept,1);
			report.generateConceptsCountSheet(concepts,2);
			report.generateConceptsLabelSheet(concepts,info,3);
			report.generateConceptsCommentSheet(concepts, info, 4);
			int numSheet = report.generatePropertiesSheet(properties,5);
			report.generatePropertiesLabelSheet(properties, info, numSheet+1);
			report.generatePropertiesCommentSheet(properties, info, numSheet+2);			
			report.generateEquPropertiesSheet(equProperties,numSheet+3);
			report.generateSubPropertiesSheet(subProperties,numSheet+4);
			report.generateInvPropertiesSheet(invProperties,numSheet+5);
			report.generatePropertiesCountSheet(properties,subProperties,invProperties,equProperties,numSheet+6);
			report.generateSubClassOfSheet(SubClassOfRelation,numSheet+7);
			report.generateSomeValueFromSheet(SomeValueFromRelation,numSheet+8);
			report.generateSomeValueFromLiteralSheet(SomeValueFromLiteralRelation,numSheet+9);
			report.generateAllValueFromSheet(AllValueFromRelation,numSheet+10);
			report.generateAllValueFromLiteralSheet(AllValueFromLiteralRelation,numSheet+11);
			report.generateMinCardinalitySheet(MinCardinalityRelation,numSheet+12);
			report.generateMinCardinalityLiteralSheet(MinCardinalityLiteralRelation,numSheet+13);
			numSheet = report.generateDomainRangeSheet(DRRelation, properties,numSheet+14);
			report.generateUsedOntologySheet(usedOntology,numSheet+1);			
			//Close File and End Write
			report.endWrite();
		} catch (Exception e) {
			new Events().error("Failed to write the ontology report", e);
		}
	
        //Salvo le informazioni utilizzate per il calcolo dei percorsi nella gerarchia
        FileDataSupport writeFileSupp = new FileDataSupport(SubClassOfRelation, datasetSupportFileDirectory + "SubclassOf.txt", datasetSupportFileDirectory + "Concepts.txt", datasetSupportFileDirectory + "EquConcepts.txt", datasetSupportFileDirectory + "EquProperties.txt", datasetSupportFileDirectory + "DR.txt", datasetSupportFileDirectory + "Properties.txt", datasetSupportFileDirectory + "DTProperties.txt");
        writeFileSupp.writeSubclass(equConcept);
        
        //Calcolo tutti i percorsi nella gerarchia
        
        InputFile subclasses = new TextInput(new FileSystemConnector(new File(datasetSupportFileDirectory + "SubclassOf.txt")));
        LongestPaths pathHierarchy = new LongestPaths(concepts, subclasses);
        pathHierarchy.writeTo(datasetSupportFileDirectory + "path.txt");
        
        writeFileSupp.writeConcept(concepts);
        writeFileSupp.writeEquclass(equConcept);
        writeFileSupp.writeEquProperty(equProperties);
        writeFileSupp.writeDR(DRRelation);
        writeFileSupp.writeProperty(properties);
	}

}
