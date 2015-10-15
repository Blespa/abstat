package it.unimib.disco.summarization.test.unit;

import it.unimib.disco.summarization.dataset.NTriple;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;


public class TripleBuilder{
	
	private Resource s;
	private Property p;
	private RDFNode o;
	private Model model;
	
	public TripleBuilder() {
		model = ModelFactory.createDefaultModel();
		s = model.createResource();
		p = model.createProperty("http://any");
		o = model.createResource();
	}

	public TripleBuilder withSubject(String subject) throws Exception{
		this.s = model.createResource(subject);
		return this;
	}
	
	public TripleBuilder withProperty(String property) throws Exception{
		this.p = model.createProperty(property);
		return this;
	}
	
	public TripleBuilder withObject(String object) {
		this.o = model.createResource(object);
		return this;
	}
	
	public TripleBuilder withTypedLiteral(String literal, String type){
		this.o = model.createTypedLiteral(literal,type);
		return this;
	}
	
	public TripleBuilder withLiteral(String literal){
		this.o = model.createLiteral(literal);
		return this;
	}
	
	public NTriple asTriple(){
		return new NTriple(model.createStatement(s, p, o));
	}
}