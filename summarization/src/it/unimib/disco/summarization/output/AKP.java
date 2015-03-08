package it.unimib.disco.summarization.output;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.RDF;

public class AKP {
	public static void main (String args []) throws IOException{

		Model model = ModelFactory.createDefaultModel();

		String csvFilePath = args[0];

		//Get all of the rows
		List<Row> rows = readCSV(csvFilePath);

		for (int i=1;i<rows.size();i++){

			Resource id = model.createResource("http://schemasummaries.org/resource/"+i);
			Resource subject = model.createResource(rows.get(i).get(Row.Entry.SUBJECT));
			Property predicate = model.createProperty(rows.get(i).get(Row.Entry.PREDICATE));
			Resource object = model.createResource(rows.get(i).get(Row.Entry.OBJECT));
			Property has_frequency = model.createProperty("http://schemasummaries.org/ontology/has_frequency");
			Literal statistic = model.createTypedLiteral(Integer.parseInt(rows.get(i).get(Row.Entry.SCORE1)));
			Resource AKP = model.createProperty("http://schemasummaries.org/ontology/AbstractKnowledgePattern");

			// creating a statement doesn't add it to the model
			Statement stmt1 = model.createStatement( id, RDF.type, RDF.Statement );
			Statement stmt2 = model.createStatement( id, RDF.subject, subject );
			Statement stmt3 = model.createStatement( id, RDF.predicate, predicate );
			Statement stmt4 = model.createStatement( id, RDF.object, object );
			Statement stmt_stat = model.createStatement( id, has_frequency, statistic);
			Statement stmt5 = model.createStatement( id, RDF.type, AKP);

			// creating a reified statement does add some triples to the model
			// ReifiedStatement rstmt = model.createReifiedStatement( stmt );
			model.add(stmt1);
			model.add(stmt2);
			model.add(stmt3);
			model.add(stmt4);
			model.add(stmt5);
			model.add(stmt_stat);

			File directory = new File (".");
			OutputStream output = new FileOutputStream(directory.getAbsolutePath()+"/output/relationCount.nt");

			model.write( output, "N-Triples", null ); // or "RDF/XML", etc.

			output.close();
		}

	}

	public static List<Row> readCSV(String rsListFile) throws IOException {
		List<Row> allFacts = new ArrayList<Row>();

		BufferedReader br = null;
		String line =  ""
				;
		String cvsSplitBy = "##";

		try {

			br = new BufferedReader(new FileReader(rsListFile));
			while ((line = br.readLine()) != null) {
				// use comma as separator
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

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return allFacts;
	}

}