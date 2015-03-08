package it.unimib.disco.summarization.output;

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

public class WriteAKPToRDF {
	
	public static void main (String args []) throws Exception{

		Model model = ModelFactory.createDefaultModel();

		String csvFilePath = args[0];
		String outputFile = args[1];

		int i=1;
		for (Row row : readCSV(csvFilePath)){

			Resource id = model.createResource("http://schemasummaries.org/resource/" + i);
			final Resource subject = model.createResource(row.get(Row.Entry.SUBJECT));

			final Property predicate = model.createProperty(row.get(Row.Entry.PREDICATE));
			final Resource object = model.createResource(row.get(Row.Entry.OBJECT));
			final Property has_frequency = model.createProperty("http://schemasummaries.org/ontology/has_frequency");
			final Literal statistic = model.createTypedLiteral(Integer.parseInt(row.get(Row.Entry.SCORE1)));
			final Resource AKP = model.createProperty("http://schemasummaries.org/ontology/AbstractKnowledgePattern");

			// creating a statement doesn't add it to the model
			final Statement stmt1 = model.createStatement( id, RDF.type, RDF.Statement );
			final Statement stmt2 = model.createStatement( id, RDF.subject, subject );
			final Statement stmt3 = model.createStatement( id, RDF.predicate, predicate );
			final Statement stmt4 = model.createStatement( id, RDF.object, object );
			final Statement stmt_stat = model.createStatement( id, has_frequency, statistic);
			final Statement stmt5 = model.createStatement( id, RDF.type, AKP);

			// creating a reified statement does add some triples to the model
			//final ReifiedStatement rstmt = model.createReifiedStatement( stmt );
			model.add(stmt1);
			model.add(stmt2);
			model.add(stmt3);
			model.add(stmt4);
			model.add(stmt5);
			model.add(stmt_stat);

			OutputStream output = new FileOutputStream(new File(outputFile));
			model.write( output, "N-Triples", null ); // or "RDF/XML", etc.
			
			output.close();
			i++;
		}

	}

	public static List<Row> readCSV(String rsListFile) throws IOException {
		
		List<Row> allFacts = new ArrayList<Row>();
		String cvsSplitBy = "##";
		
		for(String line : FileUtils.readLines(new File(rsListFile))){
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
		return allFacts;
	}

}