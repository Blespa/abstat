package it.unimib.disco.summarization.info;

import it.unimib.disco.summarization.datatype.Concept;
import it.unimib.disco.summarization.datatype.Property;
import it.unimib.disco.summarization.stemmer.SnowballStemmer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

/**
 * InfoExtractor: Extract Info Relative to Labels and Comments
 * 
 * @author Vincenzo Ferme
 */
public class InfoExtractor {
	
	private HashMap<String,HashMap<String,String>> ConceptLabel= new HashMap<String,HashMap<String,String>>();
	private List<String> ConceptLabelLang = new ArrayList<String>();
	private HashMap<String,HashMap<String,String>> ConceptComment= new HashMap<String,HashMap<String,String>>();
	private List<String> ConceptCommentLang = new ArrayList<String>();
	private HashMap<String,HashMap<String,String>> PropertyLabel= new HashMap<String,HashMap<String,String>>();
	private List<String> PropertyLabelLang = new ArrayList<String>();
	private HashMap<String,HashMap<String,String>> PropertyComment= new HashMap<String,HashMap<String,String>>();
	private List<String> PropertyCommentLang = new ArrayList<String>();
	
	
	public void setConceptInfo(Concept AllConcepts) {
		
		for(OntResource conc : AllConcepts.getExtractedConcepts()) {
			//Get Labels
			ExtendedIterator<RDFNode> Labels = conc.listLabels(null);
			//Get Comments
			ExtendedIterator<RDFNode> Comments = conc.listComments(null);
			
			//Labels
			while(Labels.hasNext()){
				RDFNode label = Labels.next();
				String [] labelInfo = label.toString().split("@");

				//Set Language Info
				String lang;
				String info;
				if(labelInfo.length>1){
					lang = labelInfo[1];
					info = labelInfo[0];
				}
				else{
					lang = "default"; //Assumed by default
					info = labelInfo[0];
				}
				
				//Update Label Lang
				if(!ConceptLabelLang.contains(lang)){
					ConceptLabelLang.add(lang);
				}
				
				updateInfo(conc.getURI(),lang,info,getConceptLabel());
			}
			
			//Comments
			while(Comments.hasNext()){
				RDFNode comment = Comments.next();
				String [] commentInfo = comment.toString().split("@");
				
				//Set Language Info
				String lang;
				String info;
				if(commentInfo.length>1){
					lang = commentInfo[1];
					info = commentInfo[0];
				}
				else{
					lang = "default"; //Assumed by default
					info = commentInfo[0];
				}
				
				//Update Label Lang
				if(!ConceptCommentLang.contains(lang)){
					ConceptCommentLang.add(lang);
				}
				
				updateInfo(conc.getURI(),lang,info,getConceptComment());
			}
			
		}
	}
	
	public void setPropertyInfo(Property AllProperty) {

		for(OntResource prop : AllProperty.getExtractedProperty()) {
			//Get Labels
			ExtendedIterator<RDFNode> Labels = prop.listLabels(null);
			//Get Comments
			ExtendedIterator<RDFNode> Comments = prop.listComments(null);

			//Labels
			while(Labels.hasNext()){
				RDFNode label = Labels.next();
				String [] labelInfo = label.toString().split("@");

				//Set Language Info
				String lang;
				String info;
				if(labelInfo.length>1){
					lang = labelInfo[1];
					info = labelInfo[0];
				}
				else{
					lang = "default"; //Assumed by default
					info = labelInfo[0];
				}
				
				//Update Label Lang
				if(!PropertyLabelLang.contains(lang)){
					PropertyLabelLang.add(lang);
				}

				updateInfo(prop.getURI(),lang,info,getPropertyLabel());
			}

			//Comments
			while(Comments.hasNext()){
				RDFNode comment = Comments.next();
				String [] commentInfo = comment.toString().split("@");

				//Set Language Info
				String lang;
				String info;
				if(commentInfo.length>1){
					lang = commentInfo[1];
					info = commentInfo[0];
				}
				else{
					lang = "default"; //Assumed by default
					info = commentInfo[0];
				}
				
				//Update Label Lang
				if(!PropertyCommentLang.contains(lang)){
					PropertyCommentLang.add(lang);
				}

				updateInfo(prop.getURI(),lang,info,getPropertyComment());
			}
		}
	}
	
	public void updateInfo(String URI, String Lang, String info, HashMap<String,HashMap<String,String>> Where){
		//Se l'URI a cui appartiene l'informazione non � presente l'aggiungo
		if(Where.get(URI)==null){
			//Count direct presence
			HashMap<String,String> count = new HashMap<String,String>();
			count.put(Lang, info);
			Where.put(URI, count);
		}
		//Se il contesto non � presente l'aggiungo
		else if(Where.get(URI).get(Lang)==null){
			Where.get(URI).put(Lang, info);
		}
	}
	
	public String stemString(String lang, String toStem){
		Class<?> stemClass = null;
		SnowballStemmer stemmer = null;
		//Associative from Lang Label to Stemmer
		Map<String, String> map = new HashMap<String, String>();
		map.put("da", "danish");
		map.put("nl", "dutch");
		map.put("en", "english");
		map.put("fi", "finnish");
		map.put("fr", "french");
		map.put("de", "german");
		map.put("hu", "hungarian");
		map.put("it", "italian");
		map.put("no", "norwegian");
		map.put("pt", "portuguese");
		map.put("no", "romanian");
		map.put("ru", "russian");
		map.put("es", "spanish");
		map.put("sv", "swedish");
		map.put("tr", "turkish");
		
		
		try {
			stemClass = Class.forName("it.unimib.disco.summarization.stemmer.ext." + map.get(lang) + "Stemmer");
		} catch (ClassNotFoundException e2) {
			e2.printStackTrace();
		}
		
		try {
			stemmer = (SnowballStemmer) stemClass.newInstance();
		} catch (InstantiationException e1) {
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		}
		
		stemmer.setCurrent(toStem);
		stemmer.stem();
		
		return stemmer.getCurrent();
	}

	public List<String> getConceptLabelLang() {
		return ConceptLabelLang;
	}

	public void setConceptLabelLang(List<String> conceptLabelLang) {
		ConceptLabelLang = conceptLabelLang;
	}

	public List<String> getConceptCommentLang() {
		return ConceptCommentLang;
	}

	public void setConceptCommentLang(List<String> conceptCommentLang) {
		ConceptCommentLang = conceptCommentLang;
	}

	public List<String> getPropertyLabelLang() {
		return PropertyLabelLang;
	}

	public void setPropertyLabelLang(List<String> propertyLabelLang) {
		PropertyLabelLang = propertyLabelLang;
	}

	public List<String> getPropertyCommentLang() {
		return PropertyCommentLang;
	}

	public void setPropertyCommentLang(List<String> propertyCommentLang) {
		PropertyCommentLang = propertyCommentLang;
	}

	public HashMap<String,HashMap<String,String>> getConceptLabel() {
		return ConceptLabel;
	}

	public void setConceptLabel(HashMap<String,HashMap<String,String>> conceptLabel) {
		ConceptLabel = conceptLabel;
	}

	public HashMap<String,HashMap<String,String>> getConceptComment() {
		return ConceptComment;
	}

	public void setConceptComment(HashMap<String,HashMap<String,String>> conceptComment) {
		ConceptComment = conceptComment;
	}

	public HashMap<String,HashMap<String,String>> getPropertyLabel() {
		return PropertyLabel;
	}

	public void setPropertyLabel(HashMap<String,HashMap<String,String>> propertyLabel) {
		PropertyLabel = propertyLabel;
	}

	public HashMap<String,HashMap<String,String>> getPropertyComment() {
		return PropertyComment;
	}

	public void setPropertyComment(HashMap<String,HashMap<String,String>> propertyComment) {
		PropertyComment = propertyComment;
	}

}
