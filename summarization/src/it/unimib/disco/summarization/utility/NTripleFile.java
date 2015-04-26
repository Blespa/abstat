package it.unimib.disco.summarization.utility;

import it.unimib.disco.summarization.starter.Events;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.semanticweb.yars.nx.parser.NxParser;

public class NTripleFile {

	private static final Pattern isAcceptable = Pattern.compile("^<.+> <.+> (?<object>.+) \\.");
	private NTripleAnalysis analysis;

	public NTripleFile(NTripleAnalysis analysis) {
		this.analysis = analysis;
	}

	public void process(File file) throws Exception {
		TextInput input = new TextInput(new FileSystemConnector(file));
		while(input.hasNextLine()){
			String line = input.nextLine();
			Matcher matcher = isAcceptable.matcher(line);
			if(!matcher.matches()){
				continue;
			}
			String rawObject = matcher.group("object");
			if(rawObject.startsWith("<")) line = line.replace(rawObject, rawObject.replace(" ", "%20"));
			try{
				NTriple triple = new NTriple(NxParser.parseNodes(line));
				analysis.track(triple);
			}catch(Exception e){
				new Events().error("error processing " + input.name(), e);
			}
		}
	}

}
