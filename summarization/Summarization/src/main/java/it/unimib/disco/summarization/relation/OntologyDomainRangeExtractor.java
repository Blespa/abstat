package it.unimib.disco.summarization.relation;

import it.unimib.disco.summarization.datatype.Concept;
import it.unimib.disco.summarization.datatype.DomainRange;
import it.unimib.disco.summarization.datatype.Property;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

/**
 * Extract domain and range relation from an ontology
 * 
 * @author Vincenzo Ferme
 */
public class OntologyDomainRangeExtractor {

	private DomainRange PropertyDomainRange = new DomainRange();

	public void setConceptsDomainRange(Concept Concepts, Property Properties) {
		
		//Used for Dynamic Computation of Domain And Range
		HashMap<String, ArrayList<OntResource>> DRProperty = new HashMap<String, ArrayList<OntResource>>();
		
		List<OntResource> AddConcepts = new ArrayList<OntResource>();
		
		//Last size of DRProperty Set 
		int lastSize = 0;

		//TODO: Propago le informazioni finch� non crescono pi� (vedere se vale il punto fisso) 
		// - Forse si pu� ottimizzare togliendo dall'insieme delle propriet� quelle per cui ad un passo non si trova nulla

		while(lastSize==0 || DRProperty.size()>lastSize){ //Finch� nuovi elementi vengono aggiunti

			Iterator<OntProperty> itP = Properties.getExtractedProperty().iterator();

			lastSize = DRProperty.size();

			while(itP.hasNext()) {
				OntProperty property = itP.next();
				String URIP = property.getURI();
				
				//Inizializzo Domain e Range
				OntResource clsD = null;
				OntResource clsR = null;
				
				//TODO: Rimuovere
				/*
				System.out.println("--------------- " + property.getLocalName() +" ---------------");
				
				Iterator iterator = DRProperty.keySet().iterator();

				while (iterator.hasNext()) {
					String key = iterator.next().toString();
					String value = DRProperty.get(key).toString();

					System.out.println(key + " " + value);
				}

				System.out.println("--------------------------------------------------");
				*/
				//TODO: Rimuovere

				//Se non ho gi� salvato dominio e range da precedenti propagazioni
				if( DRProperty.get(property.getURI())!=null )
				{
					ArrayList<OntResource> data = DRProperty.get(property.getURI());
					clsD = data.get(0);
					clsR = data.get(1);
					
					//TODO: Rimuovere
					/*
					System.out.println("DOMAIN: " + clsD);
					System.out.println("RANGE: " + clsR);
					System.out.println("----------------------------------");
					*/
					//TODO: Rimuovere

				}
				else
				{
					clsD = property.getDomain();
					clsR = property.getRange();
				}

				//Se la propriet� � --, il suo URI e quello di Domain e Range sono diversi da null e sia Dominio che Range sono --
				if( URIP!=null && property.getDomain()!=null && property.getRange()!=null && clsD.isClass()){ //TODO: && cls1.getNameSpace().compareTo(nameSpace)==0 && cls1.getDomain().getNameSpace().compareTo(nameSpace)==0 && cls1.getRange().getNameSpace().compareTo(nameSpace)==0

					//Se non ho gi� salvato dominio e range da precedenti propagazioni
					if( DRProperty.get(property.getURI())==null ){
						//TODO: Rimuovere
						//System.out.println(property.getLocalName() + ": (" + clsD.getLocalName() + ", " + clsR.getLocalName() + ")"); //- DataType: " + cls1.isDatatypeProperty() + " - ObjectProperty: " + cls1.isObjectProperty()
						//TODO: Rimuovere
						
						//Salvo le informazioni
						ArrayList<OntResource> data = new ArrayList<OntResource>();
						data.add(clsD);
						data.add(clsR);
						DRProperty.put(property.getURI(), data);
						PropertyDomainRange.setNewObtainedBy(property.getURI(), "DomainRange: " + property.getLocalName());
						
						//Salvo il tipo della propriet�
						PropertyDomainRange.setPropertyType(property.getURI(), property.getRDFType(true).getLocalName());
						
						//Aggiorno l'elenco dei concetti se ne compaiono di nuovi
						if(Concepts.getConcepts().get(clsD.getURI()) == null)  {//If Domain is a New Concept save It
							Concepts.getConcepts().put(clsD.getURI(),clsD.getLocalName());
							Concepts.setNewObtainedBy(clsD.getURI(), "Domain - " + property.getLocalName() + " (" + property.getRDFType(true).getLocalName() + ")");
							AddConcepts.add(clsD);
							//TODO: Rimuovere
							/*
							System.out.println("CLASS ADDED - DOMAIN & RANGE");
							System.out.println(clsD.getURI());
							System.out.println(clsD.getLocalName());
							System.out.println("----------------------------------");
							*/
							//TODO: Rimuovere
						}
						
						//Count Presence of Class as Domain
						Concepts.updateCounter(clsD.getURI(), "Domain");
						
						//TODO: Decidere, && clsR.getRDFType(true)!=null esclude tutte quelle esterne in pratica

						if(clsR.isClass() && Concepts.getConcepts().get(clsR.getURI()) == null)  {//If Range is a New Concept save It
							Concepts.getConcepts().put(clsR.getURI(),clsR.getLocalName());
							Concepts.setNewObtainedBy(clsR.getURI(), "Range - " + property.getLocalName() + " (" + property.getRDFType(true).getLocalName() + ")");
							AddConcepts.add(clsR);
							
							//TODO: Rimuovere
							/*
							System.out.println("CLASS ADDED - DOMAIN & RANGE");
							System.out.println(clsR.getURI());
							System.out.println(clsR.getLocalName());
							System.out.println("----------------------------------");
							*/
							//TODO: Rimuovere
						}
						
						//Count Presence of Class as Range
						if (clsR.isClass()) 
							Concepts.updateCounter(clsR.getURI(), "Range");
						
					}
				}
				
				
				//TODO: Sistemare i nomi delle variabili
				
				//Se ho Dominio e Range, Propago l'informazione alle sottopropriet� e alle propriet� inverse
				if( clsD!=null && clsR!=null ){
					//Ottengo le Sottopropriet�
					ExtendedIterator<? extends OntProperty> itSP = property.listSubProperties();

					while(itSP.hasNext()) {
						OntProperty cls11 = itSP.next();
						String URI11 = cls11.getURI();
						
						//Se la sottopropriet� ha un URI, � un -- e sia Dominio che Range sono --
						if( URI11!=null && clsD.isClass() ){ //TODO: && cls1.getNameSpace().compareTo(nameSpace)==0 && cls1.getDomain().getNameSpace().compareTo(nameSpace)==0 && cls1.getRange().getNameSpace().compareTo(nameSpace)==0

							//Se non ho gi� salvato dominio e range da precedenti propagazioni
							if( DRProperty.get(cls11.getURI())==null ){
								//TODO: Rimuovere
								//System.out.println(cls11.getLocalName() + ": (" + clsD.getLocalName() + ", " + clsR.getLocalName() + ")"); //- DataType: " + cls1.isDatatypeProperty() + " - ObjectProperty: " + cls1.isObjectProperty()
								//TODO: Rimuovere
								
								//Salvo le informazioni
								ArrayList<OntResource> data = new ArrayList<OntResource>();
								data.add(clsD);
								data.add(clsR);
								DRProperty.put(cls11.getURI(), data);
								PropertyDomainRange.setNewObtainedBy(cls11.getURI(), "DomainRange: " + cls11.getLocalName() + " - SubPropertyOf: "+ property.getLocalName());
								
								//Salvo il tipo della propriet�
								PropertyDomainRange.setPropertyType(cls11.getURI(), cls11.getRDFType(true).getLocalName());
								
								//Count Presence of Class as Domain
								Concepts.updateCounter(clsD.getURI(), "Domain - Sub");
								//Count Presence of Class as Range
								if(clsR.isClass())
									Concepts.updateCounter(clsR.getURI(), "Range - Sub");
							}
							
						}
					}

					//Ottengo le Propriet� Inverse
					ExtendedIterator<? extends OntProperty> itSP1 = property.listInverse();

					while(itSP1.hasNext()) {
						OntProperty cls11 = itSP1.next();
						String URI11 = cls11.getURI();
						
						//Se la propriet� inversa ha un URI, � un -- e sia Dominio che Range sono --
						if( URI11!=null && clsD.isClass() ){ //TODO: && cls1.getNameSpace().compareTo(nameSpace)==0 && cls1.getDomain().getNameSpace().compareTo(nameSpace)==0 && cls1.getRange().getNameSpace().compareTo(nameSpace)==0

							//Se non ho gi� salvato dominio e range da precedenti propagazioni
							if( DRProperty.get(cls11.getURI())==null ){
								//TODO: Rimuovere
								//System.out.println(cls11.getLocalName() + ": (" + clsR.getLocalName() + ", " + clsD.getLocalName() + ")"); //- DataType: " + cls1.isDatatypeProperty() + " - ObjectProperty: " + cls1.isObjectProperty()
								//TODO: Rimuovere

								//Salvo le informazioni
								ArrayList<OntResource> data = new ArrayList<OntResource>();
								data.add(clsR);
								data.add(clsD);
								DRProperty.put(cls11.getURI(), data);
								PropertyDomainRange.setNewObtainedBy(cls11.getURI(), "DomainRange: " + cls11.getLocalName() + " - InvPropertyOf: "+ property.getLocalName());
								
								//Salvo il tipo della propriet�
								PropertyDomainRange.setPropertyType(cls11.getURI(), cls11.getRDFType(true).getLocalName());
								
								//Count Presence of Class as Domain
								if(clsR.isClass())
									Concepts.updateCounter(clsR.getURI(), "Domain - Inv");
								//Count Presence of Class as Range
								Concepts.updateCounter(clsD.getURI(), "Range - Inv");
							}
							
						}
					}
				}
				//TODO: Rimuovere
				//System.out.println("----------------------------------");
				//TODO: Rimuovere
			}
		}
		
		Concepts.getExtractedConcepts().addAll(AddConcepts);
		
		PropertyDomainRange.setDRRelation(DRProperty);

	}

	public DomainRange getPropertyDomainRange() {
		return PropertyDomainRange;
	}


}
