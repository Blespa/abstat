/**
 * 
 */
package it.unimib.disco.summarization.relation;

import it.unimib.disco.summarization.datatype.Axiom;
import it.unimib.disco.summarization.datatype.Concepts;
import it.unimib.disco.summarization.datatype.DomainRange;
import it.unimib.disco.summarization.datatype.LiteralAxiom;

import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDFS;

/**
 * OntologyAxiomExtractor: Extract SomeValuesFrom, AllValueFrom, MinCardinality Relation and update the list of concept
 *
 * @author Vincenzo Ferme
 */
public class OntologyAxiomExtractor {

	private Axiom ConceptsSomeValueFrom = new Axiom();
	private Axiom ConceptsAllValueFrom = new Axiom();
	private Axiom ConceptsMinCardinality = new Axiom();
	private LiteralAxiom ConceptsMinCardinalityLiteral = new LiteralAxiom();
	private LiteralAxiom ConceptsSomeValueFromLiteral = new LiteralAxiom();
	private LiteralAxiom ConceptsAllValueFromLiteral = new LiteralAxiom();


	public void setConceptsSomeValueFrom(Concepts Concepts, DomainRange DRRelation, OntModel ontologyModel) {
		
		//Object SomeValueFrom
		
		List<OntResource> AddConcepts = new ArrayList<OntResource>();
		
		//SPARQL Query for SomeValueFrom
		String queryString = "PREFIX rdfs:<" + RDFS.getURI() + ">" +
				"PREFIX owl:<" + OWL.getURI() + ">" +
				"SELECT ?s ?p ?o WHERE {?r owl:onProperty ?p . ?r owl:someValuesFrom ?o . ?s rdfs:subClassOf ?r}";

		//Execute Query
		Query query = QueryFactory.create(queryString) ;
		QueryExecution qexec = QueryExecutionFactory.create(query, ontologyModel);

		try {

			ResultSet results = qexec.execSelect();

			//Temporary Model in Order to Construct Node for External Concept
			OntModel ontologyTempModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM, null); 

			//Extract SomeValueFrom Relation
			for ( ; results.hasNext() ; )
			{
				QuerySolution soln = results.nextSolution();
				//TODO: Rimuovere
				//ResultSetFormatter.out(System.out, results, query);
				//TODO: Rimuovere

				if(soln.get("s").isResource() && soln.get("o").isResource() && soln.get("p").isResource()){

					Resource subj = soln.getResource("s");
					String URISUBJ = subj.getURI();
					Resource prop = soln.getResource("p");
					String URIPROP = prop.getURI();
					Resource obj = soln.getResource("o");
					String URIOBJ = obj.getURI();


					//Get SomeValueFrom with not null class and property
					if( URISUBJ!=null && URIPROP!=null && URIOBJ!=null ){

						OntClass conceptSubj = ontologyTempModel.createClass(URISUBJ);
						OntProperty property = ontologyTempModel.createOntProperty(URIPROP);
						OntClass conceptObj = ontologyTempModel.createClass(URIOBJ);
						
						//Se conceptObj � Thing, verifico se posso sostituirlo con il range di property se presente
						if(conceptObj.getLocalName().equals("Thing"))
						{
							ArrayList<OntResource> domRange = DRRelation.getDRRelation().get(property.getURI());
							
							if( domRange!=null )
							{
								OntResource Range = domRange.get(1);
								conceptObj = Range.asClass();
							}

						}

						//Save SomeValueFrom Relation (conceptSubj property(SomeValueFrom) conceptObj)
						ConceptsSomeValueFrom.addAxiomRelation(conceptSubj, property, conceptObj);

						if(Concepts.getConcepts().get(URISUBJ) == null)  {//If Subject is a New Concept save It
							Concepts.getConcepts().put(URISUBJ,conceptSubj.getLocalName());
							Concepts.setNewObtainedBy(URISUBJ, "SomeValuesFrom  - " + conceptObj.getLocalName());
							AddConcepts.add(conceptSubj);
						}
						
						//Count Presence of Class as SomeValueFrom
						Concepts.updateCounter(URISUBJ, "SomeValuesFrom - Subj");

						if(Concepts.getConcepts().get(URIOBJ) == null)  {//If Object is a New Concept save It
							Concepts.getConcepts().put(URIOBJ,conceptObj.getLocalName());
							Concepts.setNewObtainedBy(URIOBJ, conceptSubj.getLocalName() + " - SomeValuesFrom");
							AddConcepts.add(conceptObj);
							
						}
						
						//Count Presence of Class as SomeValueFrom
						Concepts.updateCounter(URIOBJ, "SomeValuesFrom - Obj");
					}

				}
			}

		} finally { qexec.close() ; }
		

		//Literal SomeValueFrom

		String queryStringLiteral = "PREFIX rdfs:<" + RDFS.getURI() + ">" +
				"PREFIX owl:<" + OWL.getURI() + ">" +
				"SELECT ?s ?p ?o WHERE {?r owl:onProperty ?p . ?r owl:someValuesFrom ?o . ?s rdfs:subClassOf ?r}";

		//Execute Query
		Query queryLiteral = QueryFactory.create(queryStringLiteral) ;
		QueryExecution qexecLiteral = QueryExecutionFactory.create(queryLiteral, ontologyModel);

		try {

			ResultSet results = qexecLiteral.execSelect();

			//Temporary Model in Order to Construct Node for External Concept
			OntModel ontologyTempModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM, null); 

			//Extract SomeValueFrom Relation
			for ( ; results.hasNext() ; )
			{
				QuerySolution soln = results.nextSolution();
				//TODO: Rimuovere
				//ResultSetFormatter.out(System.out, results, queryLiteral);
				//TODO: Rimuovere

				if(soln.get("s").isResource() && soln.get("o").isLiteral() && soln.get("p").isResource()){

					Resource subj = soln.getResource("s");
					String URISUBJ = subj.getURI();
					Resource prop = soln.getResource("p");
					String URIPROP = prop.getURI();
					RDFNode obj = soln.getLiteral("o");
					String LITERALOBJ = obj.toString();


					//Get SomeValueFrom with not null class and property
					if( URISUBJ!=null && URIPROP!=null && LITERALOBJ!=null ){

						OntClass conceptSubj = ontologyTempModel.createClass(URISUBJ);
						OntProperty property = ontologyTempModel.createOntProperty(URIPROP);
						RDFNode conceptLiteral = ontologyTempModel.createClass(LITERALOBJ);

						//Save SomeValueFrom Relation (conceptSubj property(SomeValueFrom) conceptObj)
						ConceptsSomeValueFromLiteral.addAxiomRelation(conceptSubj, property, conceptLiteral);

						if(Concepts.getConcepts().get(URISUBJ) == null)  {//If Subject is a New Concept save It
							Concepts.getConcepts().put(URISUBJ,conceptSubj.getLocalName());
							Concepts.setNewObtainedBy(URISUBJ, "SomeValuesFrom (Literal) - " + conceptLiteral.toString());
							AddConcepts.add(conceptSubj);
						}

						//Count Presence of Class as SomeValueFrom
						Concepts.updateCounter(URISUBJ, "SomeValuesFrom - Subj (Literal)");
					}

				}
			}

		} finally { qexecLiteral.close() ; }

		Concepts.getExtractedConcepts().addAll(AddConcepts);
	}

	public void setConceptsAllValueFrom(Concepts Concepts, DomainRange DRRelation, OntModel ontologyModel) {
		
		//Object AllValueFrom
		
		List<OntResource> AddConcepts = new ArrayList<OntResource>();
		
		String queryString = "PREFIX rdfs:<" + RDFS.getURI() + ">" +
				"PREFIX owl:<" + OWL.getURI() + ">" +
				"SELECT ?s ?p ?o WHERE {?r owl:onProperty ?p . ?r owl:allValuesFrom ?o . ?s rdfs:subClassOf ?r}";

		//Execute Query
		Query query = QueryFactory.create(queryString) ;
		QueryExecution qexec = QueryExecutionFactory.create(query, ontologyModel);

		try {

			ResultSet results = qexec.execSelect();

			//Temporary Model in Order to Construct Node for External Concept
			OntModel ontologyTempModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM, null); 

			//Extract allValuesFrom Relation
			for ( ; results.hasNext() ; )
			{
				QuerySolution soln = results.nextSolution();
				//TODO: Rimuovere
				//ResultSetFormatter.out(System.out, results, query);
				//TODO: Rimuovere

				if(soln.get("s").isResource() && soln.get("o").isResource() && soln.get("p").isResource()){

					Resource subj = soln.getResource("s");
					String URISUBJ = subj.getURI();
					Resource prop = soln.getResource("p");
					String URIPROP = prop.getURI();
					Resource obj = soln.getResource("o");
					String URIOBJ = obj.getURI();


					//Get allValuesFrom with not null class and property
					if( URISUBJ!=null && URIPROP!=null && URIOBJ!=null ){

						OntClass conceptSubj = ontologyTempModel.createClass(URISUBJ);
						OntProperty property = ontologyTempModel.createOntProperty(URIPROP);
						OntClass conceptObj = ontologyTempModel.createClass(URIOBJ);
						
						//Se conceptObj � Thing, verifico se posso sostituirlo con il range di property se presente
						if(conceptObj.getLocalName().equals("Thing"))
						{
							ArrayList<OntResource> domRange = DRRelation.getDRRelation().get(property.getURI());
							
							if( domRange!=null )
							{
								OntResource Range = domRange.get(1);
								conceptObj = Range.asClass();
							}

						}

						//Save allValuesFrom Relation (conceptSubj property(allValuesFrom) conceptObj)
						ConceptsAllValueFrom.addAxiomRelation(conceptSubj, property, conceptObj);

						if(Concepts.getConcepts().get(URISUBJ) == null)  {//If Subject is a New Concept save It
							Concepts.getConcepts().put(URISUBJ,conceptSubj.getLocalName());
							Concepts.setNewObtainedBy(URISUBJ, "AllValuesFrom  - " + conceptObj.getLocalName());
							AddConcepts.add(conceptSubj);
						}
						
						//Count Presence of Class as AllValueFrom
						Concepts.updateCounter(URISUBJ, "AllValuesFrom - Subj");

						if(Concepts.getConcepts().get(URIOBJ) == null)  {//If Object is a New Concept save It
							Concepts.getConcepts().put(URIOBJ,conceptObj.getLocalName());
							Concepts.setNewObtainedBy(URIOBJ, conceptSubj.getLocalName() + " - AllValuesFrom");
							AddConcepts.add(conceptObj);
						}
						
						//Count Presence of Class as AllValueFrom
						Concepts.updateCounter(URIOBJ, "AllValuesFrom - Obj");
					}

				}
			}

		} finally { qexec.close() ; }
		
		//Literal AllValueFrom

		String queryStringLiteral = "PREFIX rdfs:<" + RDFS.getURI() + ">" +
				"PREFIX owl:<" + OWL.getURI() + ">" +
				"SELECT ?s ?p ?o WHERE {?r owl:onProperty ?p . ?r owl:allValuesFrom ?o . ?s rdfs:subClassOf ?r}";

		//Execute Query
		Query queryLiteral = QueryFactory.create(queryStringLiteral) ;
		QueryExecution qexecLiteral = QueryExecutionFactory.create(queryLiteral, ontologyModel);

		try {

			ResultSet results = qexecLiteral.execSelect();

			//Temporary Model in Order to Construct Node for External Concept
			OntModel ontologyTempModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM, null); 

			//Extract allValuesFrom Relation
			for ( ; results.hasNext() ; )
			{
				QuerySolution soln = results.nextSolution();
				//TODO: Rimuovere
				//ResultSetFormatter.out(System.out, results, queryLiteral);
				//TODO: Rimuovere

				if(soln.get("s").isResource() && soln.get("o").isLiteral() && soln.get("p").isResource()){

					Resource subj = soln.getResource("s");
					String URISUBJ = subj.getURI();
					Resource prop = soln.getResource("p");
					String URIPROP = prop.getURI();
					RDFNode obj = soln.getLiteral("o");
					String LITERALOBJ = obj.toString();


					//Get allValuesFrom with not null class and property
					if( URISUBJ!=null && URIPROP!=null && LITERALOBJ!=null ){

						OntClass conceptSubj = ontologyTempModel.createClass(URISUBJ);
						OntProperty property = ontologyTempModel.createOntProperty(URIPROP);
						RDFNode conceptLiteral = ontologyTempModel.createClass(LITERALOBJ);

						//Save allValuesFrom Relation (conceptSubj property(allValuesFrom) conceptObj)
						getConceptsAllValueFromLiteral().addAxiomRelation(conceptSubj, property, conceptLiteral);

						if(Concepts.getConcepts().get(URISUBJ) == null)  {//If Subject is a New Concept save It
							Concepts.getConcepts().put(URISUBJ,conceptSubj.getLocalName());
							Concepts.setNewObtainedBy(URISUBJ, "AllValuesFrom (Literal) - " + conceptLiteral.toString());
							AddConcepts.add(conceptSubj);
						}

						//Count Presence of Class as AllValueFrom
						Concepts.updateCounter(URISUBJ, "AllValuesFrom - Subj (Literal)");
					}

				}
			}

		} finally { qexecLiteral.close() ; }

		Concepts.getExtractedConcepts().addAll(AddConcepts);
	}

	public void setConceptsMinCardinality(Concepts Concepts, DomainRange DRRelation, OntModel ontologyModel) {
		
		//Object MinCardinality
		
		List<OntResource> AddConcepts = new ArrayList<OntResource>();
		
		String queryString = "PREFIX rdfs:<" + RDFS.getURI() + ">" +
				"PREFIX owl:<" + OWL.getURI() + ">" +
				"SELECT ?s ?p ?o WHERE {?r owl:onProperty ?p . ?r owl:minCardinality ?o . ?s rdfs:subClassOf ?r}";

		//Execute Query
		Query query = QueryFactory.create(queryString) ;
		QueryExecution qexec = QueryExecutionFactory.create(query, ontologyModel);

		try {

			ResultSet results = qexec.execSelect();

			//Temporary Model in Order to Construct Node for External Concept
			OntModel ontologyTempModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM, null); 

			//Extract MinCardinality Relation
			for ( ; results.hasNext() ; )
			{
				QuerySolution soln = results.nextSolution();
				//TODO: Rimuovere
				//ResultSetFormatter.out(System.out, results, query);
				//TODO: Rimuovere

				if(soln.get("s").isResource() && soln.get("o").isResource() && soln.get("p").isResource()){

					Resource subj = soln.getResource("s");
					String URISUBJ = subj.getURI();
					Resource prop = soln.getResource("p");
					String URIPROP = prop.getURI();
					Resource obj = soln.getResource("o");
					String URIOBJ = obj.getURI();


					//Get MinCardinality with not null class and property
					if( URISUBJ!=null && URIPROP!=null && URIOBJ!=null ){

						OntClass conceptSubj = ontologyTempModel.createClass(URISUBJ);
						OntProperty property = ontologyTempModel.createOntProperty(URIPROP);
						OntClass conceptObj = ontologyTempModel.createClass(URIOBJ);
						
						//Se conceptObj � Thing, verifico se posso sostituirlo con il range di property se presente
						if(conceptObj.getLocalName().equals("Thing"))
						{
							ArrayList<OntResource> domRange = DRRelation.getDRRelation().get(property.getURI());
							
							if( domRange!=null )
							{
								OntResource Range = domRange.get(1);
								conceptObj = Range.asClass();
							}

						}

						//Save MinCardinality Relation (conceptSubj property(MinCardinality) conceptObj)
						ConceptsMinCardinality.addAxiomRelation(conceptSubj, property, conceptObj);

						if(Concepts.getConcepts().get(URISUBJ) == null)  {//If Subject is a New Concept save It
							Concepts.getConcepts().put(URISUBJ,conceptSubj.getLocalName());
							Concepts.setNewObtainedBy(URISUBJ, "MinCardinality  - " + conceptObj.getLocalName());
							AddConcepts.add(conceptSubj);
						}
						
						//Count Presence of Class as MinCardinality
						Concepts.updateCounter(URISUBJ, "MinCardinality - Subj");

						if(Concepts.getConcepts().get(URIOBJ) == null)  {//If Object is a New Concept save It
							Concepts.getConcepts().put(URIOBJ,conceptObj.getLocalName());
							Concepts.setNewObtainedBy(URIOBJ, conceptSubj.getLocalName() + " - MinCardinality");
							AddConcepts.add(conceptObj);
						}
						
						//Count Presence of Class as MinCardinality
						Concepts.updateCounter(URIOBJ, "MinCardinality - Obj");
					}

				}
			}

		} finally { qexec.close() ; }
		
		//Literal MinCardinality

		String queryStringLiteral = "PREFIX rdfs:<" + RDFS.getURI() + ">" +
				"PREFIX owl:<" + OWL.getURI() + ">" +
				"SELECT ?s ?p ?o WHERE {?r owl:onProperty ?p . ?r owl:minCardinality ?o . ?s rdfs:subClassOf ?r}";

		//Execute Query
		Query queryLiteral = QueryFactory.create(queryStringLiteral) ;
		QueryExecution qexecLiteral = QueryExecutionFactory.create(queryLiteral, ontologyModel);

		try {

			ResultSet results = qexecLiteral.execSelect();

			//Temporary Model in Order to Construct Node for External Concept
			OntModel ontologyTempModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM, null); 

			//Extract MinCardinality Relation
			for ( ; results.hasNext() ; )
			{
				QuerySolution soln = results.nextSolution();
				//TODO: Rimuovere
				//ResultSetFormatter.out(System.out, results, queryLiteral);
				//TODO: Rimuovere

				if(soln.get("s").isResource() && soln.get("o").isLiteral() && soln.get("p").isResource()){

					Resource subj = soln.getResource("s");
					String URISUBJ = subj.getURI();
					Resource prop = soln.getResource("p");
					String URIPROP = prop.getURI();
					RDFNode obj = soln.getLiteral("o");
					String LITERALOBJ = obj.toString();


					//Get MinCardinality with not null class and property
					if( URISUBJ!=null && URIPROP!=null && LITERALOBJ!=null ){

						OntClass conceptSubj = ontologyTempModel.createClass(URISUBJ);
						OntProperty property = ontologyTempModel.createOntProperty(URIPROP);
						RDFNode conceptLiteral = ontologyTempModel.createClass(LITERALOBJ);

						//Save MinCardinality Relation (conceptSubj property(MinCardinality) conceptObj)
						ConceptsMinCardinalityLiteral.addAxiomRelation(conceptSubj, property, conceptLiteral);

						if(Concepts.getConcepts().get(URISUBJ) == null)  {//If Subject is a New Concept save It
							Concepts.getConcepts().put(URISUBJ,conceptSubj.getLocalName());
							Concepts.setNewObtainedBy(URISUBJ, "MinCardinality (Literal) - " + conceptLiteral.toString());
							AddConcepts.add(conceptSubj);
						}

						//Count Presence of Class as MinCardinality
						Concepts.updateCounter(URISUBJ, "MinCardinality - Subj (Literal)");
					}

				}
			}

		} finally { qexecLiteral.close() ; }

		Concepts.getExtractedConcepts().addAll(AddConcepts);
	}

	public Axiom getConceptsSomeValueFrom() {
		return ConceptsSomeValueFrom;
	}

	public Axiom getConceptsAllValueFrom() {
		return ConceptsAllValueFrom;
	}

	public Axiom getConceptsMinCardinality() {
		return ConceptsMinCardinality;
	}
	
	public LiteralAxiom getConceptsMinCardinalityLiteral() {
		return ConceptsMinCardinalityLiteral;
	}

	public LiteralAxiom getConceptsSomeValueFromLiteral() {
		return ConceptsSomeValueFromLiteral;
	}

	public void setConceptsSomeValueFromLiteral(
			LiteralAxiom conceptsSomeValueFromLiteral) {
		ConceptsSomeValueFromLiteral = conceptsSomeValueFromLiteral;
	}

	public LiteralAxiom getConceptsAllValueFromLiteral() {
		return ConceptsAllValueFromLiteral;
	}

	public void setConceptsAllValueFromLiteral(
			LiteralAxiom conceptsAllValueFromLiteral) {
		ConceptsAllValueFromLiteral = conceptsAllValueFromLiteral;
	}

}
