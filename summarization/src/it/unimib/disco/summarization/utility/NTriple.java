package it.unimib.disco.summarization.utility;

import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.RDFS;

public class NTriple{
	
	private Statement nodes;

	public NTriple(Statement triple){
		this.nodes = triple;
	}
	
	public RDFNode subject(){
		return nodes.getSubject();
	}
	
	public RDFNode property(){
		return nodes.getPredicate();
	}
	
	public RDFNode object(){
		return nodes.getObject();
	}
	
	public String dataType() {
		String datatype = this.object().asLiteral().getDatatypeURI();
		if(datatype == null) datatype = RDFS.Literal.getURI();
		return datatype;
	}
}