package it.unimib.disco.summarization.output;

import it.unimib.disco.summarization.ontology.ConceptExtractor;
import it.unimib.disco.summarization.ontology.Concepts;
import it.unimib.disco.summarization.ontology.EqConceptExtractor;
import it.unimib.disco.summarization.ontology.EquivalentConcepts;
import it.unimib.disco.summarization.ontology.OntologySubclassOfExtractor;
import it.unimib.disco.summarization.ontology.Properties;
import it.unimib.disco.summarization.ontology.PropertyExtractor;
import it.unimib.disco.summarization.ontology.SubClassOf;
import it.unimib.disco.summarization.utility.FileDataSupport;
import it.unimib.disco.summarization.utility.Model;

import java.io.File;
import java.util.Collection;

import org.apache.commons.io.FileUtils;

import com.hp.hpl.jena.ontology.OntModel;


public class ProcessOntology {

	public static void main(String[] args) throws Exception {
		Events.summarization();
		
		String owlBaseFileArg = null;
		String datasetSupportFileDirectory = null;
		
		owlBaseFileArg=args[0];
		datasetSupportFileDirectory=args[1];

		File folder = new File(owlBaseFileArg);
		Collection<File> listOfFiles = FileUtils.listFiles(folder, new String[]{"owl"}, false);
		String fileName = listOfFiles.iterator().next().getName();
		
		String owlBaseFile = "file://" + owlBaseFileArg + "/" + fileName;

		//Model
		OntModel ontologyModel = new Model(owlBaseFile,"RDF/XML").getOntologyModel();
		
		//Extract Property from Ontology Model
		PropertyExtractor pExtract = new PropertyExtractor();
		pExtract.setProperty(ontologyModel);
		
		Properties properties = new Properties();
		properties.setProperty(pExtract.getProperty());
		properties.setExtractedProperty(pExtract.getExtractedProperty());
		properties.setCounter(pExtract.getCounter());
		
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
		
		//Extract EquivalentClass from Ontology Model - Qui per considerare tutti i concetti
		EqConceptExtractor equConcepts = new EqConceptExtractor();
		equConcepts.setEquConcept(concepts, ontologyModel);
		
		EquivalentConcepts equConcept = new EquivalentConcepts();
		equConcept.setExtractedEquConcept(equConcepts.getExtractedEquConcept());
		equConcept.setEquConcept(equConcepts.getEquConcept());
		
		concepts.deleteThing();
		SubClassOfRelation.deleteThing();
		
        FileDataSupport writeFileSupp = new FileDataSupport(SubClassOfRelation, datasetSupportFileDirectory + "SubclassOf.txt", datasetSupportFileDirectory + "Concepts.txt");
        
        writeFileSupp.writeSubclass(equConcept);
        writeFileSupp.writeConcept(concepts);
	}

}
