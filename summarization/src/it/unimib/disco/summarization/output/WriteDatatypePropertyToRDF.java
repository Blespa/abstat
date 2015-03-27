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
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

public class WriteDatatypePropertyToRDF {
	public static void main (String args []) throws IOException{

		Model model = ModelFactory.createDefaultModel();

		String csvFilePath = args[0];
		String outputFilePath = args[1];
		String dataset = args[2];
		
		LDSummariesVocabulary vocabulary = new LDSummariesVocabulary(model, dataset);

		//Get all of the rows
		for (Row row : readCSV(csvFilePath)){

			try{
				Resource globalProperty = model.createResource(row.get(Row.Entry.SUBJECT));
				Resource localProperty = vocabulary.asLocalResource(globalProperty.getURI());

				Literal occurrence = model.createTypedLiteral(Integer.parseInt(row.get(Row.Entry.SCORE1)));
				Literal minTypeSubOccurrence = model.createTypedLiteral(Integer.parseInt(row.get(Row.Entry.SCORE2)));
				Literal minTypeObjOccurrence = model.createTypedLiteral(Integer.parseInt(row.get(Row.Entry.SCORE3)));
				
				//add statements to model
				model.add(localProperty, RDFS.seeAlso, globalProperty);
				model.add(localProperty, RDF.type, vocabulary.property());
				model.add(localProperty, vocabulary.occurrence(), occurrence);
				model.add(localProperty, vocabulary.subjectMinTypes(), minTypeSubOccurrence);
				model.add(localProperty, vocabulary.objectMinTypes(), minTypeObjOccurrence);
			}
			catch(Exception e){
				new Events().error("file" + csvFilePath + " row" + row, e);
			}
		}
		OutputStream output = new FileOutputStream(outputFilePath);
		model.write( output, "N-Triples", null ); // or "", etc.
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
					r.add(Row.Entry.SCORE1, row[1]);
					r.add(Row.Entry.SCORE2, row[4]);

					if(row.length == 8|| row.length == 7){

						r.add(Row.Entry.SCORE3, row[6]);
					}
					else if(row.length == 6){

						r.add(Row.Entry.SCORE3, row[5]);
					}
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
