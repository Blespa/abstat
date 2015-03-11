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
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

public class WritePropertiesOtherStatToRDF {
	public static void main (String args []) throws IOException{


		Model model = ModelFactory.createDefaultModel();
		String csvFilePath = args[0];
		String outputFilePath = args[1];

		//Get all of the rows
		for (Row row : readCSV(csvFilePath)){

			try{

				Resource subject = model.createResource(row.get(Row.Entry.SUBJECT));
				Resource signature = model.createResource("http://schemasummaries.org/ontology/Signature");
				Property has_statistic1 = model.createProperty("http://schemasummaries.org/ontology/frequency");
				Property has_statistic2 = model.createProperty("http://schemasummaries.org/ontology/ratio");
				Literal statistic1 = model.createTypedLiteral(Integer.parseInt(row.get(Row.Entry.SCORE1)));
				Literal statistic2 = model.createTypedLiteral(Double.parseDouble(row.get(Row.Entry.SCORE2)));

				//create the statements
				Statement stmt1 = model.createStatement( subject, RDF.type, signature );
				Statement stmt2 = model.createStatement( subject, RDF.type, RDFS.Class );
				Statement stmt_stat1 = model.createStatement( subject, has_statistic1, statistic1 );
				Statement stmt_stat2 = model.createStatement( subject, has_statistic2, statistic2 );

				//add statements to model
				model.add(stmt1);
				model.add(stmt2);
				model.add(stmt_stat1);
				model.add(stmt_stat2);
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
					r.add(Row.Entry.SCORE1, row[2]); 
					r.add(Row.Entry.SCORE2, row[1]);

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
