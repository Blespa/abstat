package it.unimib.disco.summarization.export;
import it.unimib.disco.summarization.ontology.LDSummariesVocabulary;
import it.unimib.disco.summarization.ontology.RDFTypeOf;

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
		String domain = args[3];
		
		LDSummariesVocabulary vocabulary = new LDSummariesVocabulary(model, dataset);
		RDFTypeOf typeOf = new RDFTypeOf(domain);

		for (Row row : readCSV(csvFilePath)){

			try{
				Resource globalProperty = model.createResource(row.get(Row.Entry.SUBJECT));
				Resource localProperty = vocabulary.asLocalDatatypeProperty(globalProperty.getURI());

				Literal occurrence = model.createTypedLiteral(Integer.parseInt(row.get(Row.Entry.SCORE1)));
				
				model.add(localProperty, RDFS.seeAlso, globalProperty);
				model.add(localProperty, RDF.type, vocabulary.property());
				model.add(localProperty, RDF.type, typeOf.resource(globalProperty.getURI()));
				model.add(localProperty, vocabulary.occurrence(), occurrence);
			}
			catch(Exception e){
				Events.summarization().error("file" + csvFilePath + " row" + row, e);
			}
		}
		OutputStream output = new FileOutputStream(outputFilePath);
		model.write( output, "N-Triples", null );
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
					allFacts.add(r);
				}
			}
			catch(Exception e){
				Events.summarization().error("file" + rsListFile + " line " + line, e);
			}
		}
		return allFacts;
	}

}
