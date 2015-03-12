package it.unimib.disco.summarization.output;

import it.unimib.disco.summarization.starter.Events;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;

public class WriteAKPToRDF {
	public static void main (String args []) throws IOException{
		
		String csvFilePath = args[0];
		String outputFilePath = args[1];
		String dataset = args[2];

		Model model = ModelFactory.createDefaultModel();
		LDSummariesVocabulary vocabulary = new LDSummariesVocabulary(model, dataset);
		
		for (Row row : readCSV(csvFilePath)){

			try{
				Resource globalSubject = model.createResource(row.get(Row.Entry.SUBJECT));
				Property globalPredicate = model.createProperty(row.get(Row.Entry.PREDICATE));
				Resource globalObject = model.createResource(row.get(Row.Entry.OBJECT));
				Literal statistic = model.createTypedLiteral(Integer.parseInt(row.get(Row.Entry.SCORE1)));
				
				Resource localSubject = vocabulary.asLocalResource(globalSubject.getURI());
				Resource localPredicate = vocabulary.asLocalResource(globalPredicate.getURI());
				Resource localObject = vocabulary.asLocalResource(globalObject.getURI());
				
				Resource akpInstance = vocabulary.akpInstance(localSubject.getURI(), localPredicate.getURI(), localObject.getURI());
				
				//add statements to model
				model.add(model.createStatement(localSubject, OWL.sameAs, globalSubject ));
				model.add(model.createStatement(localPredicate, OWL.sameAs, globalPredicate ));
				model.add(model.createStatement(localObject, OWL.sameAs, globalObject ));
				
				model.add(model.createStatement( akpInstance, RDF.type, RDF.Statement ));
				model.add(model.createStatement( akpInstance, RDF.subject, localSubject ));
				model.add(model.createStatement( akpInstance, RDF.predicate, localPredicate ));
				model.add(model.createStatement( akpInstance, RDF.object, localObject ));
				model.add(model.createStatement( akpInstance, RDF.type, vocabulary.akpConcept()));
				model.add(model.createStatement( akpInstance, vocabulary.instanceOccurrence(), statistic));
			}
			catch(Exception e){
				new Events().error("file" + csvFilePath + " row" + row, e);
			}

		}
		OutputStream output = new FileOutputStream(outputFilePath);
		model.write( output, "N-Triples", null ); // or "RDF/XML", etc.

		output.close();


	}

	public static List<Row> readCSV(String rsListFile) throws IOException {
		List<Row> allFacts = new ArrayList<Row>();

		String cvsSplitBy = "##";

		for(String line : FileUtils.readLines(new File(rsListFile))){
			try{
				String[] row = line.split(cvsSplitBy);
				Row r = new Row();

				if (row[0].contains("http")){
					r.add(Row.Entry.SUBJECT, row[0]);
					r.add(Row.Entry.PREDICATE, row[1]);
					r.add(Row.Entry.OBJECT, row[2]);
					r.add(Row.Entry.SCORE1, row[3]);

					allFacts.add(r);
				}
			}
			catch(Exception e){
				new Events().error("file" + rsListFile + " line " + line, e);
			}
		}
		return allFacts;
	}

}