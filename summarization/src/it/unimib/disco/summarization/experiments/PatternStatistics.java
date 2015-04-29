package it.unimib.disco.summarization.experiments;

import it.unimib.disco.summarization.output.Events;
import it.unimib.disco.summarization.output.LDSummariesVocabulary;

import com.hp.hpl.jena.rdf.model.ModelFactory;

public class PatternStatistics {

	public static void main(String[] args) {
		
		String dataset = args[0];
		
		new Events();
		
		LDSummariesVocabulary vocabulary = new LDSummariesVocabulary(ModelFactory.createDefaultModel(), dataset);
		
		int totalAKP = new AbstatEndpoint()
						.execute("select (count(?pattern) as ?count)"
							 + "from <" + vocabulary.graph() + "> "
					 		 + "where {"
					 		 	+ "?pattern a <"+ vocabulary.abstractKnowledgePattern().getURI() + "> ." +
					 		  "}")
					 	.next().getLiteral("count").getInt();

//		String ontology = "http://purl.org/ontology/mo/";
//		int internalAKP = new AbstatEndpoint()
//						.execute("select (count(?pattern) as ?count)"
//							 + "from <" + vocabulary.graph() + "> "
//							 + "where {"
//						 		+ "?pattern a <"+ vocabulary.abstractKnowledgePattern().getURI() + "> ."
//							 	+ "?pattern <"+ vocabulary.subject()+ "> ?localSubject ."
//							 	+ "?localSubject <"+ RDFS.seeAlso + "> ?subject ."
//							 	+ "?pattern <"+ vocabulary.object()+ "> ?localObject ."
//							 	+ "?localObject a <"+ vocabulary.type() + "> . "
//							 	+ "?localObject <"+ RDFS.seeAlso + "> ?object . "
//							 	+ "filter not exists {"
//							 		+ "?localObject a <" + vocabulary.datatype() + "> . "
//						 		+ "} "
//							 	+ "filter regex(?subject, '" + ontology + "') "
//							 	+ "filter regex(?object, '" + ontology + "') "
//							 + "}")
//					 	.next().getLiteral("count").getInt();
		
		int datatypeAKP = new AbstatEndpoint()
						.execute("select (count(?pattern) as ?count)"
							 + "from <" + vocabulary.graph() + "> "
					 		 + "where {"
					 		 	+ "?pattern a <"+ vocabulary.abstractKnowledgePattern().getURI() + "> . "
					 		 	+ "?pattern <"+ vocabulary.object() + "> ?object . "
					 		 	+ "?object a <"+ vocabulary.datatype() + "> . "
					 		 + "}")
			 		 	.next().getLiteral("count").getInt();
		
		System.out.println("AKP:\t" + totalAKP);
		System.out.println("Object AKP:\t" + (totalAKP - datatypeAKP));
		System.out.println("Datatype AKP:\t" + datatypeAKP);
	}
}
