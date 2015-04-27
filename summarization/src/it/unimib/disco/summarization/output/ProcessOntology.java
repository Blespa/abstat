package it.unimib.disco.summarization.output;

import it.unimib.disco.summarization.datatype.Concepts;
import it.unimib.disco.summarization.datatype.EquProperty;
import it.unimib.disco.summarization.datatype.EquivalentConcepts;
import it.unimib.disco.summarization.datatype.InvProperty;
import it.unimib.disco.summarization.datatype.Properties;
import it.unimib.disco.summarization.datatype.SubClassOf;
import it.unimib.disco.summarization.datatype.SubProperty;
import it.unimib.disco.summarization.extraction.ConceptExtractor;
import it.unimib.disco.summarization.extraction.EqConceptExtractor;
import it.unimib.disco.summarization.extraction.EqPropertyExtractor;
import it.unimib.disco.summarization.extraction.InvPropertyExtractor;
import it.unimib.disco.summarization.extraction.PropertyExtractor;
import it.unimib.disco.summarization.extraction.SubPropertyExtractor;
import it.unimib.disco.summarization.relation.OntologySubclassOfExtractor;
import it.unimib.disco.summarization.utility.FileDataSupport;
import it.unimib.disco.summarization.utility.Model;

import java.io.File;
import java.util.Collection;

import org.apache.commons.io.FileUtils;

import com.hp.hpl.jena.ontology.OntModel;


public class ProcessOntology {

	public static void main(String[] args) throws Exception {
		new Events();
		
		String owlBaseFileArg = null;
		String datasetSupportFileDirectory = null;
		
		owlBaseFileArg=args[0];
		datasetSupportFileDirectory=args[1];

		File folder = new File(owlBaseFileArg);
		Collection<File> listOfFiles = FileUtils.listFiles(folder, new String[]{"owl"}, false);
		String fileName = listOfFiles.iterator().next().getName();
		
		String owlBaseFile = "file://" + owlBaseFileArg + "/" + fileName;

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
