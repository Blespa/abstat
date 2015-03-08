package it.unimib.disco.summarization.output;

import it.unimib.disco.summarization.starter.Events;

import java.io.File;
import java.io.FileOutputStream;
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

public class WriteConceptsToRDF {
	public static void main (String args []) throws Exception{

		new Events();
		
		Model model = ModelFactory.createDefaultModel();

		String inputFilePath = args[0];
		String outputFilePath = args[1];

		for (Row row : readCSV(inputFilePath)){

			final Resource subject = model.createResource(row.get(Row.Entry.SUBJECT));
			final Resource signature = model.createResource("http://schemasummaries.org/ontology/Signature");
			final Property has_statistic1 = model.createProperty("http://schemasummaries.org/ontology/has_frequency");
			final Property has_statistic2 = model.createProperty("http://schemasummaries.org/ontology/has_percentage_minimalType");
			final Literal statistic1 = model.createTypedLiteral(Integer.parseInt(row.get(Row.Entry.SCORE1)));
			final Literal statistic2 = model.createTypedLiteral(Double.parseDouble(row.get(Row.Entry.SCORE2)));

			// creating a statement doesn't add it to the model
			final Statement stmt = model.createStatement( subject, RDF.type, signature );
			final Statement stmt_stat1 = model.createStatement( subject, has_statistic1, statistic1 );
			final Statement stmt_stat2 = model.createStatement( subject, has_statistic2, statistic2 );

			model.add(stmt);
			model.add(stmt_stat1);
			model.add(stmt_stat2);

			OutputStream output = new FileOutputStream(outputFilePath);
			model.write( output, "N-Triples", null); // or "RDF/XML", etc.
			output.close();
		}
	}

	public static List<Row> readCSV(String rsListFile) throws Exception {
		
		List<Row> allFacts = new ArrayList<Row>();
		String cvsSplitBy = "##";
		
		for(String line : FileUtils.readLines(new File(rsListFile))){
			String[] row = line.split(cvsSplitBy);
			Row r = new Row();
			if (row[0].contains("http")){
				r.add(Row.Entry.SUBJECT, row[0]);
				r.add(Row.Entry.SCORE1, row[1]);
				r.add(Row.Entry.SCORE2, row[2]);

				allFacts.add(r);
			}
		}
		return allFacts;
	}

}