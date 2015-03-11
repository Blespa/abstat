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

public class WriteAKPToRDF {
	public static void main (String args []) throws IOException{

		Model model = ModelFactory.createDefaultModel();
		String csvFilePath = args[0];
		String outputFilePath = args[1];

		for (Row row : readCSV(csvFilePath)){

			try{
				Resource id = model.createResource("http://schemasummaries.org/resource/AKP_"+
						new RDFResource(row.get(Row.Entry.SUBJECT)).resource()+"_"+
						new RDFResource(row.get(Row.Entry.PREDICATE)).resource()+"_"+
						new RDFResource(row.get(Row.Entry.OBJECT)).resource());
				
				Resource subject = model.createResource(row.get(Row.Entry.SUBJECT));
				Property predicate = model.createProperty(row.get(Row.Entry.PREDICATE));
				Resource object = model.createResource(row.get(Row.Entry.OBJECT));
				Property has_frequency = model.createProperty("http://schemasummaries.org/ontology/instancOccurrence");
				Literal statistic = model.createTypedLiteral(Integer.parseInt(row.get(Row.Entry.SCORE1)));
				Resource AKP = model.createProperty("http://schemasummaries.org/ontology/AbstractKnowledgePattern");

				// create statements
				Statement stmt1 = model.createStatement( id, RDF.type, RDF.Statement );
				Statement stmt2 = model.createStatement( id, RDF.subject, subject );
				Statement stmt3 = model.createStatement( id, RDF.predicate, predicate );
				Statement stmt4 = model.createStatement( id, RDF.object, object );
				Statement stmt_stat = model.createStatement( id, has_frequency, statistic);
				Statement stmt5 = model.createStatement( id, RDF.type, AKP);

				//add statements to model
				model.add(stmt1);
				model.add(stmt2);
				model.add(stmt3);
				model.add(stmt4);
				model.add(stmt5);
				model.add(stmt_stat);
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