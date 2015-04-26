package it.unimib.disco.summarization.utility;

import it.unimib.disco.summarization.starter.Events;

import java.io.File;

import org.apache.commons.io.IOUtils;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Statement;

public class NTripleFile {

	private NTripleAnalysis analysis;

	public NTripleFile(NTripleAnalysis analysis) {
		this.analysis = analysis;
	}

	public void process(File file) throws Exception {
		TextInput input = new TextInput(new FileSystemConnector(file));
		while(input.hasNextLine()){
			String line = input.nextLine();
			String[] splitted = line.split("##");
			String subject = splitted[0];
			String property = splitted[1];
			String object = splitted[2];
			String datatype = "";
			
			if(splitted.length > 3){
				datatype = "^^<" + splitted[3] + ">";
			}

			if(!object.startsWith("\"")){
				object = "<" + object + ">";
			}
			
			line = "<" + subject + "> <" + property + "> " + object + datatype + " .";  
			
			Model model = ModelFactory.createDefaultModel();
			model.read(IOUtils.toInputStream(line) ,null, "N-TRIPLES");
			Statement statement = model.listStatements().next();
			
			try{
				NTriple triple = new NTriple(statement);
				analysis.track(triple);
			}catch(Exception e){
				new Events().error("error processing " + line + " from " + input.name(), e);
			}
		}
	}

}
