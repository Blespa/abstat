package it.unimib.disco.write;

import java.io.BufferedReader;
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
import com.hp.hpl.jena.rdf.model.ReifiedStatement;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;

public class WriteCSV_RDF {
	public static void main (String args []) throws IOException{

		Model model = ModelFactory.createDefaultModel();

		String csvFilePath = "/Users/anisarula/Documents/git/schema-summaries/temp/relationCount.csv";

		//Get all of the rows
		List<Row> rows = readCSV(csvFilePath);

		for (int i=1;i<rows.size();i++){
			System.out.println(rows.size());
			Resource id = model.createResource("http://example.org/resource/"+i);
			final Resource subject = model.createResource(rows.get(i).get(Row.Entry.SUBJECT));
			System.out.println(rows.get(i).get(Row.Entry.PREDICATE));
			final Property predicate = model.createProperty(rows.get(i).get(Row.Entry.PREDICATE));
			final Resource object = model.createResource(rows.get(i).get(Row.Entry.OBJECT));
			final Property has_statistic = model.createProperty("http://example.org/property/has_value");
			final Literal statistic = model.createTypedLiteral(rows.get(i).get(Row.Entry.SCORE));
			final Property has_knowledgePattern = model.createProperty("http://example.org/property/has_knowledgePattern");

			// creating a statement doesn't add it to the model
			final Statement stmt = model.createStatement( subject, predicate, object );
			final Statement stmt_stat = model.createStatement( id, has_statistic, statistic );

			// creating a reified statement does add some triples to the model
			final ReifiedStatement rstmt = model.createReifiedStatement( stmt );

			model.add(stmt_stat);
			model.add( id, has_knowledgePattern, rstmt );
			
			OutputStream output = new FileOutputStream("/Users/anisarula/Documents/git/schema-summaries/temp/relationCount.rdf");
			model.write( output, "N-Triples", null ); // or "RDF/XML", etc.
			output.close();
		}

	}

	public static List<Row> readCSV(String rsListFile) {
		List<Row> allFacts = new ArrayList<Row>();

		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";

		try {

			br = new BufferedReader(new FileReader(rsListFile));
			while ((line = br.readLine()) != null) {
				// use comma as separator
				String[] row = line.split(cvsSplitBy);
				Row r = new Row();
				if (row.length>0){r.add(Row.Entry.SUBJECT, row[0]);}
				if (row.length>1){r.add(Row.Entry.PREDICATE, row[1]);}
				if (row.length>2){r.add(Row.Entry.OBJECT, row[2]);}
				if (row.length>3){r.add(Row.Entry.SCORE, row[3]);}

				allFacts.add(r);
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
