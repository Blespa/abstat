package it.unimib.disco.summarization.utility;

import it.unimib.disco.summarization.datatype.Axiom;
import it.unimib.disco.summarization.datatype.Concepts;
import it.unimib.disco.summarization.datatype.DomainRange;
import it.unimib.disco.summarization.datatype.EquivalentConcepts;
import it.unimib.disco.summarization.datatype.EquProperty;
import it.unimib.disco.summarization.datatype.InvProperty;
import it.unimib.disco.summarization.datatype.LiteralAxiom;
import it.unimib.disco.summarization.datatype.Properties;
import it.unimib.disco.summarization.datatype.SubClassOf;
import it.unimib.disco.summarization.datatype.SubProperty;
import it.unimib.disco.summarization.info.InfoExtractor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import jxl.CellView;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 * CreateExcel: Create a Report In Excel
 */
public class CreateExcel {
	private WritableCellFormat timesBold;
	private WritableCellFormat times;
	private String inputFile;
	private WritableWorkbook workbook;

	public void setOutputFile(String inputFile) {
		this.inputFile = inputFile;
	}

	public void startWrite() 
			throws IOException, WriteException {
		
		File file = new File(inputFile);
		WorkbookSettings wbSettings = new WorkbookSettings();

		wbSettings.setLocale(new Locale("en", "EN"));

		workbook = Workbook.createWorkbook(file, wbSettings);
	}

	public void endWrite() 
			throws IOException, WriteException {
		
		workbook.write();
		workbook.close();

	}

	private void createLabel(WritableSheet sheet, String [] Labels) 
			throws WriteException {
		// Lets create a times font
		WritableFont times12pt = new WritableFont(WritableFont.TIMES, 12);
		// Define the cell format
		times = new WritableCellFormat(times12pt);
		// Lets automatically wrap the cells
		times.setWrap(true);

		// Create create a bold font
		WritableFont times12ptBold = new WritableFont(WritableFont.TIMES, 12, WritableFont.BOLD, false);
		timesBold = new WritableCellFormat(times12ptBold);
		// Lets automatically wrap the cells
		timesBold.setWrap(true);

		CellView cv = new CellView();
		cv.setFormat(times);
		cv.setFormat(timesBold);
//		cv.setAutosize(true);

		// Write down Labels
		int numLabel = 0;
		for (String Label : Labels){
			addCaption(sheet, numLabel, 0, Label);
			numLabel++;
		}

	}
	
	/**
	 * Generate sheet for Concept
	 * 
	 * @throws WriteException 
	 */
	public void generateConceptsSheet(Concepts AllConcepts, int NumSheet) throws WriteException
	{
		//Concepts are in the first Sheet
		workbook.createSheet("Concepts", NumSheet);
		WritableSheet excelSheet = workbook.getSheet(NumSheet);
		String [] Labels = {"LocalName", "URI", "Explanation - Extraction Rule"};
		createLabel(excelSheet, Labels);
		
		Iterator<String> cIter = AllConcepts.getConcepts().keySet().iterator();
		
		int i = 1; //Start write ad second line, after Labels
		int maxLenght[] = new int[Labels.length]; //Max length of content per cell
		
		while (cIter.hasNext()) {
			String key = cIter.next().toString();
			String value = AllConcepts.getConcepts().get(key).toString();
			
			// First column
			addLabel(excelSheet, 0, i, value);
			// Second column
			addLabel(excelSheet, 1, i, key);
			//Third column
			String ObtainedBy = AllConcepts.getObtainedBy().get(key); //Get ObtainedBy info for Current Concept
			if(ObtainedBy!=null && ObtainedBy.length()>0 && !ObtainedBy.equals("null"))
				addLabel(excelSheet, 2, i, ObtainedBy);
			else
				addLabel(excelSheet, 2, i, "-");
			
			//Update Max Length
			if( value.length()>maxLenght[0] )
				maxLenght[0] = value.length();
			if( key.length()>maxLenght[1] )
				maxLenght[1] = key.length();
			if( ObtainedBy!=null && ObtainedBy.length()>maxLenght[2] )
				maxLenght[2] = ObtainedBy.length();
			
			i++;
		}
		
		//Set Width of Cells based on String Lenght
		setWidthForCell(excelSheet,maxLenght,Labels);
	}
	
	
	/**
	 * Generate sheet for EquConcepts
	 * 
	 * @throws WriteException 
	 */
	public void generateEquConceptsSheet(EquivalentConcepts equConcepts, int NumSheet) throws WriteException
	{
		//Properties are in the second Sheet
		workbook.createSheet("Equivalent Class", NumSheet);
		WritableSheet excelSheet = workbook.getSheet(NumSheet);
		
		//Set labels dynamically based on max length of list
		int maxSubProp = 0;
		Iterator<OntResource> spIter = equConcepts.getExtractedEquConcept().keySet().iterator();
		
		while (spIter.hasNext()) {
			OntResource key = spIter.next();
			List<OntResource> value = equConcepts.getExtractedEquConcept().get(key);
			
			if(value.size()>maxSubProp)
				maxSubProp = value.size();
		}
		
		List<String> LabelsList = new ArrayList<String>();
		LabelsList.add("LocalName (URI)");
		
		for(int lab = 0; lab<maxSubProp; lab++){
			LabelsList.add("EquClass (URI)");
		}
				
		String[] Labels = new String[ LabelsList.size() ];
		LabelsList.toArray( Labels );

		createLabel(excelSheet, Labels);
		
		
		Iterator<OntResource> pIter = equConcepts.getExtractedEquConcept().keySet().iterator();
		
		int i = 1; //Start write ad second line, after Labels
		int maxLenght[] = new int[Labels.length]; //Max length of content per cell
		
		while (pIter.hasNext()) {
			OntResource key = pIter.next();
			List<OntResource> value = equConcepts.getExtractedEquConcept().get(key);
			int column = 1;
			
			if( value.size()>0 ) //Il dato concetto ha concetti equivalenti
			{
				// First column
				addLabel(excelSheet, 0, i, key.getLocalName() + " (" + key.getURI() + ")");
				
				//Update Max Length
				if( (key.getLocalName() + " (" + key.getURI() + ")").length()>maxLenght[0] )
					maxLenght[0] = (key.getLocalName() + " (" + key.getURI() + ")").length();
				
				for( OntResource subP : value){
					addLabel(excelSheet, column, i, subP.getLocalName() + " (" + subP.getURI() + ")");
					
					//Update Max Length
					if( ( subP.getLocalName() + " (" + subP.getURI() + ")").length()>maxLenght[column] )
						maxLenght[column] = ( subP.getLocalName() + " (" + subP.getURI() + ")").length();
					
					column++;
				}
				
				i++;
			}
		}
		
		//Set Width of Cells based on String Lenght
		setWidthForCell(excelSheet,maxLenght,Labels);
	}
	
	/**
	 * Generate sheet for Concept Total Count
	 * 
	 * @throws WriteException 
	 */
	public void generateConceptsCountSheet(Concepts AllConcepts, int NumSheet) throws WriteException
	{
		//Concepts are in the first Sheet
		workbook.createSheet("Concepts-Count", NumSheet);
		WritableSheet excelSheet = workbook.getSheet(NumSheet);
		String [] Labels = {"LocalName", "URI", "Direct", "Equivalent Class", "SubClassOf (# Of Subclasses)", "SubClassOf (# Of Superclasses)", "SomeValuesFrom - Subj", "SomeValuesFrom - Subj (Literal)", "SomeValuesFrom - Obj", "AllValuesFrom - Subj", "AllValuesFrom - Subj (Literal)", "AllValuesFrom - Obj", "MinCardinality - Subj", "MinCardinality - Subj (Literal)", "MinCardinality - Obj", "Domain", "Range", "Domain - Sub", "Range - Sub", "Domain - Inv", "Range - Inv", "Total"};
		createLabel(excelSheet, Labels);
		
		Iterator<String> cIter = AllConcepts.getConcepts().keySet().iterator();
		
		int i = 1; //Start write ad second line, after Labels
		int maxLenght[] = new int[Labels.length]; //Max length of content per cell
		
		while (cIter.hasNext()) {
			String key = cIter.next().toString();
			String value = AllConcepts.getConcepts().get(key).toString();
			
			// First column
			addLabel(excelSheet, 0, i, value);
			// Second column
			addLabel(excelSheet, 1, i, key);
			
			//Count
			int Total = 0; //Sum of single count
			//Search Count Labels for current concept ad set cells accordingly
			for( int count = 2; count<Labels.length-1; count++ ){
				if(AllConcepts.getCounter().get(key)!=null && AllConcepts.getCounter().get(key).get(Labels[count])!=null){
					addNumber(excelSheet, count, i, AllConcepts.getCounter().get(key).get(Labels[count]));
					if(!Labels[count].equals("SubClassOf (# Of Superclasses)") && !Labels[count].equals("Equivalent Class") && !Labels[count].equals("MinCardinality - Subj (Literal)") && !Labels[count].equals("AllValuesFrom - Subj (Literal)") && !Labels[count].equals("SomeValuesFrom - Subj (Literal)")) 
						Total = Total + Integer.valueOf(AllConcepts.getCounter().get(key).get(Labels[count]));
				}
				else
					addNumber(excelSheet, count, i, 0);
			}
			
			//Total
			addNumber(excelSheet, Labels.length-1, i, Total);
			
			//Update Max Length for String Column
			if( value.length()>maxLenght[0] )
				maxLenght[0] = value.length();
			if( key.length()>maxLenght[1] )
				maxLenght[1] = key.length();
			
			i++;
		}
		
		//Set Width of Cells based on String Lenght
		setWidthForCell(excelSheet,maxLenght,Labels);
	}
	
	/**
	 * Generate sheet for Concept Labels
	 * 
	 * @throws WriteException 
	 */
	public void generateConceptsLabelSheet(Concepts AllConcepts, InfoExtractor info, int NumSheet) throws WriteException
	{
		workbook.createSheet("Concepts-Labels", NumSheet);
		WritableSheet excelSheet = workbook.getSheet(NumSheet);

		//Set labels dynamically based on max length of list of Labels

		List<String> LabelsList = new ArrayList<String>();
		LabelsList.add("LocalName (Stem)");
		LabelsList.add("URI");
		LabelsList.addAll(info.getConceptLabelLang());
		int enPos = 0; //Position of en Label
		
		for(String label : LabelsList){
			if(label.equals("en"))
				break;			
			enPos++;
		}
		
		//Se � stata trovata
		if( enPos<LabelsList.size() ){
			//Porto la label "en" al primo posto
			String toReplace = LabelsList.set(2, "en");
			//Sposto quella attualmente presente al primo posto, al posto di "en"
			LabelsList.set(enPos, toReplace);
		}

		String[] Labels = new String[ LabelsList.size() ];
		LabelsList.toArray( Labels );

		createLabel(excelSheet, Labels);
		
		Iterator<String> cIter = AllConcepts.getConcepts().keySet().iterator();
		
		int i = 1; //Start write ad second line, after Labels
		int maxLenght[] = new int[Labels.length]; //Max length of content per cell
		int totalPerLang[] = new int[Labels.length-2]; //Total Label per Lang (Same order as Labels array, except first two element)
		
		while (cIter.hasNext()) {
			String key = cIter.next().toString();
			String value = AllConcepts.getConcepts().get(key).toString();
			
			// First column
			addLabel(excelSheet, 0, i, value + " (" + info.stemString("en", value) + ")");
			// Second column
			addLabel(excelSheet, 1, i, key);
			
			//Labels
			//Search Label for current concept and Lang ad set cells accordingly
			for( int count = 2; count<Labels.length; count++ ){
				if(info.getConceptLabel().get(key)!=null && info.getConceptLabel().get(key).get(Labels[count])!=null){
					addLabel(excelSheet, count, i, info.getConceptLabel().get(key).get(Labels[count]));
					//Update Total Count
					totalPerLang[count-2] = totalPerLang[count-2] + 1;
					
					//Update Max Length for String Column
					if( info.getConceptLabel().get(key).get(Labels[count]).length()>maxLenght[count] )
						maxLenght[count] = info.getConceptLabel().get(key).get(Labels[count]).length();
				}
			}
			
			//Update Max Length for String Column
			if( (value + " (" + info.stemString("en", value) + ")").length()>maxLenght[0] )
				maxLenght[0] = (value + " (" + info.stemString("en", value) + ")").length();
			if( key.length()>maxLenght[1] )
				maxLenght[1] = key.length();
			
			i++;
		}
		
		//Total
		addLabel(excelSheet, 1, i+1, "Total");
		for(int tot = 0; tot<totalPerLang.length; tot++){
			addNumber(excelSheet, tot+2, i+1, totalPerLang[tot]);
		}
		
		//Set Width of Cells based on String Lenght
		setWidthForCell(excelSheet,maxLenght,Labels);
	}
	
	/**
	 * Generate sheet for Concept Comments
	 * 
	 * @throws WriteException 
	 */
	public void generateConceptsCommentSheet(Concepts AllConcepts, InfoExtractor info, int NumSheet) throws WriteException
	{
		workbook.createSheet("Concepts-Comments", NumSheet);
		WritableSheet excelSheet = workbook.getSheet(NumSheet);

		//Set labels dynamically based on max length of list of Labels

		List<String> CommentsList = new ArrayList<String>();
		CommentsList.add("LocalName (Stem)");
		CommentsList.add("URI");
		CommentsList.addAll(info.getConceptCommentLang());
		int enPos = 0; //Position of en Comments
		
		for(String comment : CommentsList){
			if(comment.equals("en"))
				break;			
			enPos++;
		}
		
		//Se � stata trovata
		if( enPos<CommentsList.size() ){
			//Porto il comment "en" al primo posto
			String toReplace = CommentsList.set(2, "en");
			//Sposto quello attualmente presente al primo posto, al posto di "en"
			CommentsList.set(enPos, toReplace);
		}

		String[] Comments = new String[ CommentsList.size() ];
		CommentsList.toArray( Comments );

		createLabel(excelSheet, Comments);
		
		Iterator<String> cIter = AllConcepts.getConcepts().keySet().iterator();
		
		int i = 1; //Start write ad second line, after Comments
		int maxLenght[] = new int[Comments.length]; //Max length of content per cell
		int totalPerLang[] = new int[Comments.length-2]; //Total Comments per Lang (Same order as Comments array, except first two element)
		
		while (cIter.hasNext()) {
			String key = cIter.next().toString();
			String value = AllConcepts.getConcepts().get(key).toString();
			
			// First column
			addLabel(excelSheet, 0, i, value + " (" + info.stemString("en", value) + ")");
			// Second column
			addLabel(excelSheet, 1, i, key);
			
			//Comments
			//Search Comment for current concept and Lang ad set cells accordingly
			for( int count = 2; count<Comments.length; count++ ){
				if(info.getConceptComment().get(key)!=null && info.getConceptComment().get(key).get(Comments[count])!=null){
					addLabel(excelSheet, count, i, info.getConceptComment().get(key).get(Comments[count]));
					//Update Total Count
					totalPerLang[count-2] = totalPerLang[count-2] + 1;
					
					//Update Max Length for String Column
					if( info.getConceptComment().get(key).get(Comments[count]).length()>maxLenght[count] )
						maxLenght[count] = info.getConceptComment().get(key).get(Comments[count]).length();
				}
			}
			
			//Update Max Length for String Column
			if( (value + " (" + info.stemString("en", value) + ")").length()>maxLenght[0] )
				maxLenght[0] = (value + " (" + info.stemString("en", value) + ")").length();
			if( key.length()>maxLenght[1] )
				maxLenght[1] = key.length();
			
			i++;
		}
		
		//Total
		addLabel(excelSheet, 1, i+1, "Total");
		for(int tot = 0; tot<totalPerLang.length; tot++){
			addNumber(excelSheet, tot+2, i+1, totalPerLang[tot]);
		}
		
		//Set Width of Cells based on String Lenght
		setWidthForCell(excelSheet,maxLenght,Comments);
	}
	
	/**
	 * Generate sheets for Property. The number of sheet depend on different type of property present
	 * 
	 * @throws WriteException 
	 */
	public int generatePropertiesSheet(Properties AllProperty, int NumSheet) throws WriteException
	{
		
		//Determino il numero di fogli da inserire per le propriet�
		List<OntProperty> extractedProp = AllProperty.getExtractedProperty();
		Iterator<OntProperty> ePropIt = extractedProp.iterator();
		HashMap<String,String> propType = new HashMap<String,String>();
		
		int NumSheetProp = 0; //Conta il numero di pagine da aggiungere per le propriet�
		
		while (ePropIt.hasNext()) {
			OntProperty prop = ePropIt.next();
			if(propType.get(prop.getRDFType(true).getLocalName())==null) //Se ancora non � conteggiata
			{
				NumSheetProp++;
				propType.put(prop.getRDFType(true).getLocalName(),"");
			}
		}
		
		Object[] propTypeSet = propType.keySet().toArray();
		
		for(int kprop=0; kprop<NumSheetProp; kprop++){
			
			String currType = (String) propTypeSet[kprop];
			
			workbook.createSheet("Properties - " + currType, NumSheet+kprop);
			WritableSheet excelSheet = workbook.getSheet(NumSheet+kprop);					
			
			//Filtro le propriet� in base al tipo corrente
			List<OntProperty> extractedPropType = AllProperty.getExtractedProperty();
			Iterator<OntProperty> ePropTypeIt = extractedPropType.iterator();
			HashMap<String,String> currTypeProp = new HashMap<String,String>();
			HashMap<String,String> dataTypePropRange = new HashMap<String,String>();
			
			while (ePropTypeIt.hasNext()) {
				OntProperty prop = ePropTypeIt.next();
				if(prop.getRDFType(true).getLocalName().toString().equals(currType)){
					currTypeProp.put(prop.getURI(), prop.getLocalName());
					
					if(currType.equals("DatatypeProperty")) //Save Range
					{
						OntResource range = prop.getRange();
						String description = "";
						if(range != null) description = range.getLocalName() + ": " + range.getURI();
						dataTypePropRange.put(prop.getURI(), description);
					}
				}
			}
			
			List<String> Labels = new ArrayList<String>();
			Labels.add("LocalName");
			Labels.add("URI");
			
			if(currType.equals("DatatypeProperty"))
			{
				Labels.add("Range");
			}
			
			String[] LabelsArr = Labels.toArray(new String[Labels.size()]);  
			
			createLabel(excelSheet, LabelsArr);
			
			Iterator<String> cIter = currTypeProp.keySet().iterator();
			
			int i = 1; //Start write ad second line, after Labels
			int maxLenght[] = new int[Labels.size()]; //Max length of content per cell
			
			while (cIter.hasNext()) {
				String key = cIter.next().toString();
				String value = AllProperty.getProperty().get(key).toString();
				
				// First column
				addLabel(excelSheet, 0, i, value);
				// Second column
				addLabel(excelSheet, 1, i, key);
				
				if(currType.equals("DatatypeProperty"))
				{
					addLabel(excelSheet, 2, i, dataTypePropRange.get(key));
							
					if( dataTypePropRange.get(key).length()>maxLenght[2] )
						maxLenght[2] = dataTypePropRange.get(key).length();
				}
				
				//Update Max Length
				if( value.length()>maxLenght[0] )
					maxLenght[0] = value.length();
				if( key.length()>maxLenght[1] )
					maxLenght[1] = key.length();
				
				i++;
			}
			
			//Set Width of Cells based on String Lenght
			setWidthForCell(excelSheet,maxLenght,LabelsArr);
		}
		
		return NumSheet+NumSheetProp-1;
	}
	
	/**
	 * Generate sheet for Properties Labels
	 * 
	 * @throws WriteException 
	 */
	public void generatePropertiesLabelSheet(Properties AllProperty, InfoExtractor info, int NumSheet) throws WriteException
	{
		workbook.createSheet("Properties-Labels", NumSheet);
		WritableSheet excelSheet = workbook.getSheet(NumSheet);

		//Set labels dynamically based on max length of list of Labels

		List<String> LabelsList = new ArrayList<String>();
		LabelsList.add("LocalName (Stem)");
		LabelsList.add("URI");
		LabelsList.addAll(info.getPropertyLabelLang());
		int enPos = 0; //Position of en Label
		
		for(String label : LabelsList){
			if(label.equals("en"))
				break;			
			enPos++;
		}
		
		//Se � stata trovata
		if( enPos<LabelsList.size() ){
			//Porto la label "en" al primo posto
			String toReplace = LabelsList.set(2, "en");
			//Sposto quella attualmente presente al primo posto, al posto di "en"
			LabelsList.set(enPos, toReplace);
		}

		String[] Labels = new String[ LabelsList.size() ];
		LabelsList.toArray( Labels );

		createLabel(excelSheet, Labels);
		
		Iterator<String> cIter = AllProperty.getProperty().keySet().iterator();
		
		int i = 1; //Start write ad second line, after Labels
		int maxLenght[] = new int[Labels.length]; //Max length of content per cell
		int totalPerLang[] = new int[Labels.length-2]; //Total Label per Lang (Same order as Labels array, except first two element)
		
		while (cIter.hasNext()) {
			String key = cIter.next().toString();
			String value = AllProperty.getProperty().get(key).toString();
			
			// First column
			addLabel(excelSheet, 0, i, value + " (" + info.stemString("en", value) + ")");
			// Second column
			addLabel(excelSheet, 1, i, key);
			
			//Labels
			//Search Label for current property and Lang ad set cells accordingly
			for( int count = 2; count<Labels.length; count++ ){
				if(info.getPropertyLabel().get(key)!=null && info.getPropertyLabel().get(key).get(Labels[count])!=null){
					addLabel(excelSheet, count, i, info.getPropertyLabel().get(key).get(Labels[count]));
					//Update Total Count
					totalPerLang[count-2] = totalPerLang[count-2] + 1;
					
					//Update Max Length for String Column
					if( info.getPropertyLabel().get(key).get(Labels[count]).length()>maxLenght[count] )
						maxLenght[count] = info.getPropertyLabel().get(key).get(Labels[count]).length();
				}
			}
			
			//Update Max Length for String Column
			if( (value + " (" + info.stemString("en", value) + ")").length()>maxLenght[0] )
				maxLenght[0] = (value + " (" + info.stemString("en", value) + ")").length();
			if( key.length()>maxLenght[1] )
				maxLenght[1] = key.length();
			
			i++;
		}
		
		//Total
		addLabel(excelSheet, 1, i+1, "Total");
		for(int tot = 0; tot<totalPerLang.length; tot++){
			addNumber(excelSheet, tot+2, i+1, totalPerLang[tot]);
		}
		
		//Set Width of Cells based on String Lenght
		setWidthForCell(excelSheet,maxLenght,Labels);
	}
	
	/**
	 * Generate sheet for Concept Properties
	 * 
	 * @throws WriteException 
	 */
	public void generatePropertiesCommentSheet(Properties AllProperty, InfoExtractor info, int NumSheet) throws WriteException
	{
		workbook.createSheet("Properties-Comments", NumSheet);
		WritableSheet excelSheet = workbook.getSheet(NumSheet);

		//Set labels dynamically based on max length of list of Labels

		List<String> CommentsList = new ArrayList<String>();
		CommentsList.add("LocalName (Stem)");
		CommentsList.add("URI");
		CommentsList.addAll(info.getPropertyCommentLang());
		int enPos = 0; //Position of en Comments
		
		for(String comment : CommentsList){
			if(comment.equals("en"))
				break;			
			enPos++;
		}
		
		//Se � stata trovata
		if( enPos<CommentsList.size() ){
			//Porto il comment "en" al primo posto
			String toReplace = CommentsList.set(2, "en");
			//Sposto quello attualmente presente al primo posto, al posto di "en"
			CommentsList.set(enPos, toReplace);
		}

		String[] Comments = new String[ CommentsList.size() ];
		CommentsList.toArray( Comments );

		createLabel(excelSheet, Comments);
		
		Iterator<String> cIter = AllProperty.getProperty().keySet().iterator();
		
		int i = 1; //Start write ad second line, after Comments
		int maxLenght[] = new int[Comments.length]; //Max length of content per cell
		int totalPerLang[] = new int[Comments.length-2]; //Total Comments per Lang (Same order as Comments array, except first two element)
		
		while (cIter.hasNext()) {
			String key = cIter.next().toString();
			String value = AllProperty.getProperty().get(key).toString();
			
			// First column
			addLabel(excelSheet, 0, i, value + " (" + info.stemString("en", value) + ")");
			// Second column
			addLabel(excelSheet, 1, i, key);
			
			//Comments
			//Search Comment for current property and Lang ad set cells accordingly
			for( int count = 2; count<Comments.length; count++ ){
				if(info.getPropertyComment().get(key)!=null && info.getPropertyComment().get(key).get(Comments[count])!=null){
					addLabel(excelSheet, count, i, info.getPropertyComment().get(key).get(Comments[count]));
					//Update Total Count
					totalPerLang[count-2] = totalPerLang[count-2] + 1;
					
					//Update Max Length for String Column
					if( info.getPropertyComment().get(key).get(Comments[count]).length()>maxLenght[count] )
						maxLenght[count] = info.getPropertyComment().get(key).get(Comments[count]).length();
				}
			}
			
			//Update Max Length for String Column
			if( (value + " (" + info.stemString("en", value) + ")").length()>maxLenght[0] )
				maxLenght[0] = (value + " (" + info.stemString("en", value) + ")").length();
			if( key.length()>maxLenght[1] )
				maxLenght[1] = key.length();
			
			i++;
		}
		
		//Total
		addLabel(excelSheet, 1, i+1, "Total");
		for(int tot = 0; tot<totalPerLang.length; tot++){
			addNumber(excelSheet, tot+2, i+1, totalPerLang[tot]);
		}
		
		//Set Width of Cells based on String Lenght
		setWidthForCell(excelSheet,maxLenght,Comments);
	}
	
	/**
	 * Generate sheet for EquProperty
	 * 
	 * @throws WriteException 
	 */
	public void generateEquPropertiesSheet(EquProperty equProperties, int NumSheet) throws WriteException
	{
		//Properties are in the second Sheet
		workbook.createSheet("EquProperties", NumSheet);
		WritableSheet excelSheet = workbook.getSheet(NumSheet);
		
		//Set labels dynamically based on max length of list
		int maxSubProp = 0;
		Iterator<OntProperty> spIter = equProperties.getExtractedEquProperty().keySet().iterator();
		
		while (spIter.hasNext()) {
			OntProperty key = spIter.next();
			List<OntProperty> value = equProperties.getExtractedEquProperty().get(key);
			
			if(value.size()>maxSubProp)
				maxSubProp = value.size();
		}
		
		List<String> LabelsList = new ArrayList<String>();
		LabelsList.add("LocalName (URI)");
		
		for(int lab = 0; lab<maxSubProp; lab++){
			LabelsList.add("EquProperty (URI)");
		}
				
		String[] Labels = new String[ LabelsList.size() ];
		LabelsList.toArray( Labels );

		createLabel(excelSheet, Labels);
		
		
		Iterator<OntProperty> pIter = equProperties.getExtractedEquProperty().keySet().iterator();
		
		int i = 1; //Start write ad second line, after Labels
		int maxLenght[] = new int[Labels.length]; //Max length of content per cell
		
		while (pIter.hasNext()) {
			OntProperty key = pIter.next();
			List<OntProperty> value = equProperties.getExtractedEquProperty().get(key);
			int column = 1;
			
			if( value.size()>0 ) //La data propriet� ha sottopropriet�
			{
				// First column
				addLabel(excelSheet, 0, i, key.getLocalName() + " (" + key.getURI() + ")");
				
				//Update Max Length
				if( (key.getLocalName() + " (" + key.getURI() + ")").length()>maxLenght[0] )
					maxLenght[0] = (key.getLocalName() + " (" + key.getURI() + ")").length();
				
				for( OntProperty subP : value){
					addLabel(excelSheet, column, i, subP.getLocalName() + " (" + subP.getURI() + ")");
					
					//Update Max Length
					if( ( subP.getLocalName() + " (" + subP.getURI() + ")").length()>maxLenght[column] )
						maxLenght[column] = ( subP.getLocalName() + " (" + subP.getURI() + ")").length();
					
					column++;
				}
				
				i++;
			}
		}
		
		//Set Width of Cells based on String Lenght
		setWidthForCell(excelSheet,maxLenght,Labels);
	}
	
	/**
	 * Generate sheet for SubProperty
	 * 
	 * @throws WriteException 
	 */
	public void generateSubPropertiesSheet(SubProperty SubProperty, int NumSheet) throws WriteException
	{
		//Properties are in the second Sheet
		workbook.createSheet("SubProperties", NumSheet);
		WritableSheet excelSheet = workbook.getSheet(NumSheet);
		
		//Set labels dynamically based on max length of list
		int maxSubProp = 0;
		Iterator<OntProperty> spIter = SubProperty.getExtractedSubProperty().keySet().iterator();
		
		while (spIter.hasNext()) {
			OntProperty key = spIter.next();
			List<OntProperty> value = SubProperty.getExtractedSubProperty().get(key);
			
			if(value.size()>maxSubProp)
				maxSubProp = value.size();
		}
		
		List<String> LabelsList = new ArrayList<String>();
		LabelsList.add("LocalName (URI)");
		
		for(int lab = 0; lab<maxSubProp; lab++){
			LabelsList.add("SubProperty (URI)");
		}
				
		String[] Labels = new String[ LabelsList.size() ];
		LabelsList.toArray( Labels );

		createLabel(excelSheet, Labels);
		
		
		Iterator<OntProperty> pIter = SubProperty.getExtractedSubProperty().keySet().iterator();
		
		int i = 1; //Start write ad second line, after Labels
		int maxLenght[] = new int[Labels.length]; //Max length of content per cell
		
		while (pIter.hasNext()) {
			OntProperty key = pIter.next();
			List<OntProperty> value = SubProperty.getExtractedSubProperty().get(key);
			int column = 1;
			
			if( value.size()>0 ) //La data propriet� ha sottopropriet�
			{
				// First column
				addLabel(excelSheet, 0, i, key.getLocalName() + " (" + key.getURI() + ")");
				
				//Update Max Length
				if( (key.getLocalName() + " (" + key.getURI() + ")").length()>maxLenght[0] )
					maxLenght[0] = (key.getLocalName() + " (" + key.getURI() + ")").length();
				
				for( OntProperty subP : value){
					addLabel(excelSheet, column, i, subP.getLocalName() + " (" + subP.getURI() + ")");
					
					//Update Max Length
					if( ( subP.getLocalName() + " (" + subP.getURI() + ")").length()>maxLenght[column] )
						maxLenght[column] = ( subP.getLocalName() + " (" + subP.getURI() + ")").length();
					
					column++;
				}
				
				i++;
			}
		}
		
		//Set Width of Cells based on String Lenght
		setWidthForCell(excelSheet,maxLenght,Labels);
	}
	
	/**
	 * Generate sheet for InvProperty
	 * 
	 * @throws WriteException 
	 */
	public void generateInvPropertiesSheet(InvProperty invProperties, int NumSheet) throws WriteException
	{
		//Properties are in the second Sheet
		workbook.createSheet("InvProperties", NumSheet);
		WritableSheet excelSheet = workbook.getSheet(NumSheet);
		
		//Set labels dynamically based on max length of list
		int maxSubProp = 0;
		Iterator<OntProperty> spIter = invProperties.getExtractedInvProperty().keySet().iterator();
		
		while (spIter.hasNext()) {
			OntProperty key = spIter.next();
			List<OntProperty> value = invProperties.getExtractedInvProperty().get(key);
			
			if(value.size()>maxSubProp)
				maxSubProp = value.size();
		}
		
		List<String> LabelsList = new ArrayList<String>();
		LabelsList.add("LocalName (URI)");
		
		for(int lab = 0; lab<maxSubProp; lab++){
			LabelsList.add("InvProperty (URI)");
		}
				
		String[] Labels = new String[ LabelsList.size() ];
		LabelsList.toArray( Labels );

		createLabel(excelSheet, Labels);
		
		
		Iterator<OntProperty> pIter = invProperties.getExtractedInvProperty().keySet().iterator();
		
		int i = 1; //Start write ad second line, after Labels
		int maxLenght[] = new int[Labels.length]; //Max length of content per cell
		
		while (pIter.hasNext()) {
			OntProperty key = pIter.next();
			List<OntProperty> value = invProperties.getExtractedInvProperty().get(key);
			int column = 1;
			
			if( value.size()>0 ) //La data propriet� ha propriet� inverse
			{
				// First column
				addLabel(excelSheet, 0, i, key.getLocalName() + " (" + key.getURI() + ")");
				
				//Update Max Length
				if( (key.getLocalName() + " (" + key.getURI() + ")").length()>maxLenght[0] )
					maxLenght[0] = (key.getLocalName() + " (" + key.getURI() + ")").length();
				
				for( OntProperty subP : value){
					addLabel(excelSheet, column, i, subP.getLocalName() + " (" + subP.getURI() + ")");
					
					//Update Max Length
					if( ( subP.getLocalName() + " (" + subP.getURI() + ")").length()>maxLenght[column] )
						maxLenght[column] = ( subP.getLocalName() + " (" + subP.getURI() + ")").length();
					
					column++;
				}
				
				i++;
			}
		}
		
		//Set Width of Cells based on String Lenght
		setWidthForCell(excelSheet,maxLenght,Labels);
	}
	
	/**
	 * Generate sheet for Property Total Count
	 * 
	 * @throws WriteException 
	 */
	public void generatePropertiesCountSheet(Properties AllProperty, SubProperty SubProperty, InvProperty invProperties, EquProperty equProperties, int NumSheet) throws WriteException
	{
		//Concepts are in the first Sheet
		workbook.createSheet("Property-Count", NumSheet);
		WritableSheet excelSheet = workbook.getSheet(NumSheet);
		String [] Labels = {"LocalName", "URI", "SubProperties", "InvProperties", "EquivProperty"};
		createLabel(excelSheet, Labels);
		
		Iterator<String> cIter = AllProperty.getProperty().keySet().iterator();
		
		int i = 1; //Start write ad second line, after Labels
		int maxLenght[] = new int[Labels.length]; //Max length of content per cell
		
		while (cIter.hasNext()) {
			String key = cIter.next().toString();
			String value = AllProperty.getProperty().get(key).toString();
			
			// First column
			addLabel(excelSheet, 0, i, value);
			// Second column
			addLabel(excelSheet, 1, i, key);
			
			//Count
			int SubPropertyCount = 0;
			//SubPropery			
			HashMap<String,Integer> subProperyCount = SubProperty.getCounter().get(key);
			
			if( subProperyCount!=null ){			
				Integer subCount = subProperyCount.get("SubProperty");
				
				if( subCount!=null )
					SubPropertyCount = Integer.valueOf(subCount);
			}
			
			addNumber(excelSheet, 2, i, SubPropertyCount);
			
			int InvPropertyCount = 0;
			//InvProperty
			HashMap<String,Integer> invPropertyCount = invProperties.getCounter().get(key);
			if( invPropertyCount!=null ){
				Integer invCount = invPropertyCount.get("InvProperty");
				
				if( invCount!=null )
					InvPropertyCount = Integer.valueOf(invCount);
			}
			
			addNumber(excelSheet, 3, i, InvPropertyCount);
			
			int EquivPropertyCount = 0;
			//EquivProperty
			HashMap<String,Integer> equivPropertyCount = equProperties.getCounter().get(key);
			if( equivPropertyCount!=null ){
				Integer equCount = equivPropertyCount.get("EquivProperty");
				
				if( equCount!=null )
					EquivPropertyCount = Integer.valueOf(equCount);
			}
			
			addNumber(excelSheet, 4, i, EquivPropertyCount);
			
			//Update Max Length for String Column
			if( value.length()>maxLenght[0] )
				maxLenght[0] = value.length();
			if( key.length()>maxLenght[1] )
				maxLenght[1] = key.length();
			
			i++;
		}
		
		//Set Width of Cells based on String Lenght
		setWidthForCell(excelSheet,maxLenght,Labels);
	}
	
	/**
	 * Generate sheet for SubClassOf Relation
	 * 
	 * @throws WriteException 
	 */
	public void generateSubClassOfSheet(SubClassOf SubClassOfRelation, int NumSheet) throws WriteException
	{
		//Properties are in the third Sheet
		workbook.createSheet("SubClassOf", NumSheet);
		WritableSheet excelSheet = workbook.getSheet(NumSheet);
		String [] Labels = {"Property", "Subject", "Relation", "Object", "URISubject", "URIObject", "Explanation - Extraction Rule"};
		createLabel(excelSheet, Labels);
		
		Iterator<List<OntClass>> ScIter = SubClassOfRelation.getConceptsSubclassOf().iterator();
		
		int i = 1; //Start write ad second line, after Labels
		int maxLenght[] = new int[Labels.length]; //Max length of content per cell
		
		while (ScIter.hasNext()) {
			List<OntClass> curEl = ScIter.next();
			
			// First column
			addLabel(excelSheet, 0, i, "");
			// Second column
			addLabel(excelSheet, 1, i, curEl.get(0).getLocalName());
			// Third column
			addLabel(excelSheet, 2, i, "subclassOf");
			// Fourth column
			addLabel(excelSheet, 3, i, curEl.get(1).getLocalName());
			// Fifth column
			addLabel(excelSheet, 4, i, curEl.get(0).getURI());
			// Sixth column
			addLabel(excelSheet, 5, i, curEl.get(1).getURI());
			// Seventh column
			addLabel(excelSheet, 6, i, "subclassOf");
			
			//Update Max Length
			maxLenght[0] = "Property".length(); //No content, only label
			
			if( curEl.get(0).getLocalName().length()>maxLenght[1] )
				maxLenght[1] = curEl.get(0).getLocalName().length();
			
			maxLenght[2] = "subclassOf".length();
			
			if( curEl.get(1).getLocalName().length()>maxLenght[3] )
				maxLenght[3] = curEl.get(1).getLocalName().length();
			
			if( curEl.get(0).getURI().length()>maxLenght[4] )
				maxLenght[4] = curEl.get(0).getURI().length();
			
			if( curEl.get(1).getURI().length()>maxLenght[5] )
				maxLenght[5] = curEl.get(1).getURI().length();
			
			maxLenght[6] = "subclassOf".length();
			
			i++;
		}
		
		//Set Width of Cells based on String Lenght
		setWidthForCell(excelSheet,maxLenght,Labels);
	}
	
	/**
	 * Generate sheet for SomeValueFrom Relation
	 * 
	 * @throws WriteException 
	 */
	public void generateSomeValueFromSheet(Axiom SomeValueFromRelation, int NumSheet) throws WriteException
	{
		//Properties are in the fourth Sheet
		workbook.createSheet("SomeValuesFrom", NumSheet);
		WritableSheet excelSheet = workbook.getSheet(NumSheet);
		String [] Labels = {"Property", "Subject", "Relation", "Object", "URIProperty", "URISubject", "URIObject", "Explanation - Extraction Rule"};
		createLabel(excelSheet, Labels);
		
		int i = 1; //Start write ad second line, after Labels
		int maxLenght[] = new int[Labels.length]; //Max length of content per cell
		
		for (List<Resource> row : SomeValueFromRelation.getAxiom()) {
			
			// First column
			addLabel(excelSheet, 0, i, row.get(1).getLocalName());
			// Second column
			addLabel(excelSheet, 1, i, row.get(0).getLocalName());
			// Third column
			addLabel(excelSheet, 2, i, row.get(1).getLocalName());
			// Fourth column
			addLabel(excelSheet, 3, i, row.get(2).getLocalName());
			// Fifth column
			addLabel(excelSheet, 4, i, row.get(1).getURI());
			// Sixth column
			addLabel(excelSheet, 5, i, row.get(0).getURI());
			// Seventh column
			addLabel(excelSheet, 6, i, row.get(2).getURI());
			// Eighth column
			addLabel(excelSheet, 7, i, "SomeValuesFrom");
			
			
			//Update Max Length
			
			if( row.get(1).getLocalName().length()>maxLenght[0] )
				maxLenght[0] = row.get(1).getLocalName().length();
			
			if( row.get(0).getLocalName().length()>maxLenght[1] )
				maxLenght[1] = row.get(0).getLocalName().length();
			
			if( row.get(1).getLocalName().length()>maxLenght[2] )
				maxLenght[2] = row.get(1).getLocalName().length();
			
			if( row.get(2).getLocalName().length()>maxLenght[3] )
				maxLenght[3] = row.get(2).getLocalName().length();
			
			if( row.get(1).getURI().length()>maxLenght[4] )
				maxLenght[4] = row.get(1).getURI().length();
			
			if( row.get(0).getURI().length()>maxLenght[5] )
				maxLenght[5] = row.get(0).getURI().length();
			
			if( row.get(2).getURI().length()>maxLenght[6] )
				maxLenght[6] = row.get(2).getURI().length();
			
			maxLenght[7] = "SomeValuesFrom".length();
			
			i++;
		}
		
		//Set Width of Cells based on String Lenght
		setWidthForCell(excelSheet,maxLenght,Labels);
	}
	
	
	/**
	 * Generate sheet for SomeValueFrom Literal Relation
	 * 
	 * @throws WriteException 
	 */
	public void generateSomeValueFromLiteralSheet(LiteralAxiom SomeValueFromLiteralRelation, int NumSheet) throws WriteException
	{
		//Properties are in the sixth Sheet
		workbook.createSheet("SomeValuesFrom (Literal)", NumSheet);
		WritableSheet excelSheet = workbook.getSheet(NumSheet);
		String [] Labels = {"Property", "Subject", "Relation", "Object", "URIProperty", "URISubject", "DataTypeObject", "Explanation - Extraction Rule"};
		createLabel(excelSheet, Labels);
		
		int i = 1; //Start write ad second line, after Labels
		int maxLenght[] = new int[Labels.length]; //Max length of content per cell
		
		for (List<RDFNode> row : SomeValueFromLiteralRelation.getLiteralAxiom()) {

			// First column
			addLabel(excelSheet, 0, i, ((Resource) row.get(1)).getLocalName());
			// Second column
			addLabel(excelSheet, 1, i, ((Resource) row.get(0)).getLocalName());
			// Third column
			addLabel(excelSheet, 2, i, ((Resource) row.get(1)).getLocalName());
			// Fourth column
			addLabel(excelSheet, 3, i, row.get(2).toString().substring(0, row.get(2).toString().indexOf("^^")));
			// Fifth column
			addLabel(excelSheet, 4, i, ((Resource) row.get(1)).getURI());
			// Sixth column
			addLabel(excelSheet, 5, i, ((Resource) row.get(0)).getURI());
			// Seventh column
			addLabel(excelSheet, 6, i, row.get(2).toString().substring(row.get(2).toString().indexOf("^^")+2, row.get(2).toString().length()));
			// Eighth column
			addLabel(excelSheet, 7, i, "SomeValuesFrom Literal");
			
			
			//Update Max Length
			
			if( ((Resource) row.get(1)).getLocalName().length()>maxLenght[0] )
				maxLenght[0] = ((Resource) row.get(1)).getLocalName().length();
			
			if( ((Resource) row.get(0)).getLocalName().length()>maxLenght[1] )
				maxLenght[1] = ((Resource) row.get(0)).getLocalName().length();
			
			if( ((Resource) row.get(1)).getLocalName().length()>maxLenght[2] )
				maxLenght[2] = ((Resource) row.get(1)).getLocalName().length();
			
			if( row.get(2).toString().substring(0, row.get(2).toString().indexOf("^^")).length()>maxLenght[3] )
				maxLenght[3] = row.get(2).toString().substring(0, row.get(2).toString().indexOf("^^")).length();
			
			if( ((Resource) row.get(1)).getURI().length()>maxLenght[4] )
				maxLenght[4] = ((Resource) row.get(1)).getURI().length();
			
			if( ((Resource) row.get(0)).getURI().length()>maxLenght[5] )
				maxLenght[5] = ((Resource) row.get(0)).getURI().length();
			
			if( row.get(2).toString().substring(row.get(2).toString().indexOf("^^")+2, row.get(2).toString().length()).length()>maxLenght[6] )
				maxLenght[6] = row.get(2).toString().substring(row.get(2).toString().indexOf("^^")+2, row.get(2).toString().length()).length();
			
			maxLenght[7] = "SomeValuesFrom Literal".length();
			
			i++;
		}
		
		//Set Width of Cells based on String Lenght
		setWidthForCell(excelSheet,maxLenght,Labels);
	}
	
	/**
	 * Generate sheet for AllValueFrom Relation
	 * 
	 * @throws WriteException 
	 */
	public void generateAllValueFromSheet(Axiom AllValueFromRelation, int NumSheet) throws WriteException
	{
		//Properties are in the fifth Sheet
		workbook.createSheet("AllValuesFrom", NumSheet);
		WritableSheet excelSheet = workbook.getSheet(NumSheet);
		String [] Labels = {"Property", "Subject", "Relation", "Object", "URIProperty", "URISubject", "URIObject", "Explanation - Extraction Rule"};
		createLabel(excelSheet, Labels);
		
		int i = 1; //Start write ad second line, after Labels
		int maxLenght[] = new int[Labels.length]; //Max length of content per cell
		
		for (List<Resource> row : AllValueFromRelation.getAxiom()) {
			
			// First column
			addLabel(excelSheet, 0, i, row.get(1).getLocalName());
			// Second column
			addLabel(excelSheet, 1, i, row.get(0).getLocalName());
			// Third column
			addLabel(excelSheet, 2, i, row.get(1).getLocalName());
			// Fourth column
			addLabel(excelSheet, 3, i, row.get(2).getLocalName());
			// Fifth column
			addLabel(excelSheet, 4, i, row.get(1).getURI());
			// Sixth column
			addLabel(excelSheet, 5, i, row.get(0).getURI());
			// Seventh column
			addLabel(excelSheet, 6, i, row.get(2).getURI());
			// Eighth column
			addLabel(excelSheet, 7, i, "AllValuesFrom");
			
			
			//Update Max Length
			
			if( row.get(1).getLocalName().length()>maxLenght[0] )
				maxLenght[0] = row.get(1).getLocalName().length();
			
			if( row.get(0).getLocalName().length()>maxLenght[1] )
				maxLenght[1] = row.get(0).getLocalName().length();
			
			if( row.get(1).getLocalName().length()>maxLenght[2] )
				maxLenght[2] = row.get(1).getLocalName().length();
			
			if( row.get(2).getLocalName().length()>maxLenght[3] )
				maxLenght[3] = row.get(2).getLocalName().length();
			
			if( row.get(1).getURI().length()>maxLenght[4] )
				maxLenght[4] = row.get(1).getURI().length();
			
			if( row.get(0).getURI().length()>maxLenght[5] )
				maxLenght[5] = row.get(0).getURI().length();
			
			if( row.get(2).getURI().length()>maxLenght[6] )
				maxLenght[6] = row.get(2).getURI().length();
			
			maxLenght[7] = "AllValuesFrom".length();
			
			i++;
		}
		
		//Set Width of Cells based on String Lenght
		setWidthForCell(excelSheet,maxLenght,Labels);
	}
	
	/**
	 * Generate sheet for AllValueFrom Literal Relation
	 * 
	 * @throws WriteException 
	 */
	public void generateAllValueFromLiteralSheet(LiteralAxiom AllValueFromLiteralRelation, int NumSheet) throws WriteException
	{
		//Properties are in the sixth Sheet
		workbook.createSheet("AllValuesFrom (Literal)", NumSheet);
		WritableSheet excelSheet = workbook.getSheet(NumSheet);
		String [] Labels = {"Property", "Subject", "Relation", "Object", "URIProperty", "URISubject", "DataTypeObject", "Explanation - Extraction Rule"};
		createLabel(excelSheet, Labels);
		
		int i = 1; //Start write ad second line, after Labels
		int maxLenght[] = new int[Labels.length]; //Max length of content per cell
		
		for (List<RDFNode> row : AllValueFromLiteralRelation.getLiteralAxiom()) {

			// First column
			addLabel(excelSheet, 0, i, ((Resource) row.get(1)).getLocalName());
			// Second column
			addLabel(excelSheet, 1, i, ((Resource) row.get(0)).getLocalName());
			// Third column
			addLabel(excelSheet, 2, i, ((Resource) row.get(1)).getLocalName());
			// Fourth column
			addLabel(excelSheet, 3, i, row.get(2).toString().substring(0, row.get(2).toString().indexOf("^^")));
			// Fifth column
			addLabel(excelSheet, 4, i, ((Resource) row.get(1)).getURI());
			// Sixth column
			addLabel(excelSheet, 5, i, ((Resource) row.get(0)).getURI());
			// Seventh column
			addLabel(excelSheet, 6, i, row.get(2).toString().substring(row.get(2).toString().indexOf("^^")+2, row.get(2).toString().length()));
			// Eighth column
			addLabel(excelSheet, 7, i, "AllValuesFrom Literal");
			
			
			//Update Max Length
			
			if( ((Resource) row.get(1)).getLocalName().length()>maxLenght[0] )
				maxLenght[0] = ((Resource) row.get(1)).getLocalName().length();
			
			if( ((Resource) row.get(0)).getLocalName().length()>maxLenght[1] )
				maxLenght[1] = ((Resource) row.get(0)).getLocalName().length();
			
			if( ((Resource) row.get(1)).getLocalName().length()>maxLenght[2] )
				maxLenght[2] = ((Resource) row.get(1)).getLocalName().length();
			
			if( row.get(2).toString().substring(0, row.get(2).toString().indexOf("^^")).length()>maxLenght[3] )
				maxLenght[3] = row.get(2).toString().substring(0, row.get(2).toString().indexOf("^^")).length();
			
			if( ((Resource) row.get(1)).getURI().length()>maxLenght[4] )
				maxLenght[4] = ((Resource) row.get(1)).getURI().length();
			
			if( ((Resource) row.get(0)).getURI().length()>maxLenght[5] )
				maxLenght[5] = ((Resource) row.get(0)).getURI().length();
			
			if( row.get(2).toString().substring(row.get(2).toString().indexOf("^^")+2, row.get(2).toString().length()).length()>maxLenght[6] )
				maxLenght[6] = row.get(2).toString().substring(row.get(2).toString().indexOf("^^")+2, row.get(2).toString().length()).length();
			
			maxLenght[7] = "AllValuesFrom Literal".length();
			
			i++;
		}
		
		//Set Width of Cells based on String Lenght
		setWidthForCell(excelSheet,maxLenght,Labels);
	}
	
	/**
	 * Generate sheet for MinCardinality Relation
	 * 
	 * @throws WriteException 
	 */
	public void generateMinCardinalitySheet(Axiom MinCardinalityRelation, int NumSheet) throws WriteException
	{
		//Properties are in the sixth Sheet
		workbook.createSheet("MinCardinality", NumSheet);
		WritableSheet excelSheet = workbook.getSheet(NumSheet);
		String [] Labels = {"Property", "Subject", "Relation", "Object", "URIProperty", "URISubject", "URIObject", "Explanation - Extraction Rule"};
		createLabel(excelSheet, Labels);
		
		int i = 1; //Start write ad second line, after Labels
		int maxLenght[] = new int[Labels.length]; //Max length of content per cell
		
		for (List<Resource> row : MinCardinalityRelation.getAxiom()) {

			// First column
			addLabel(excelSheet, 0, i, row.get(1).getLocalName());
			// Second column
			addLabel(excelSheet, 1, i, row.get(0).getLocalName());
			// Third column
			addLabel(excelSheet, 2, i, row.get(1).getLocalName());
			// Fourth column
			addLabel(excelSheet, 3, i, row.get(2).getLocalName());
			// Fifth column
			addLabel(excelSheet, 4, i, row.get(1).getURI());
			// Sixth column
			addLabel(excelSheet, 5, i, row.get(0).getURI());
			// Seventh column
			addLabel(excelSheet, 6, i, row.get(2).getURI());
			// Eighth column
			addLabel(excelSheet, 7, i, "MinCardinality");
			
			
			//Update Max Length
			
			if( row.get(1).getLocalName().length()>maxLenght[0] )
				maxLenght[0] = row.get(1).getLocalName().length();
			
			if( row.get(0).getLocalName().length()>maxLenght[1] )
				maxLenght[1] = row.get(0).getLocalName().length();
			
			if( row.get(1).getLocalName().length()>maxLenght[2] )
				maxLenght[2] = row.get(1).getLocalName().length();
			
			if( row.get(2).getLocalName().length()>maxLenght[3] )
				maxLenght[3] = row.get(2).getLocalName().length();
			
			if( row.get(1).getURI().length()>maxLenght[4] )
				maxLenght[4] = row.get(1).getURI().length();
			
			if( row.get(0).getURI().length()>maxLenght[5] )
				maxLenght[5] = row.get(0).getURI().length();
			
			if( row.get(2).getURI().length()>maxLenght[6] )
				maxLenght[6] = row.get(2).getURI().length();
			
			maxLenght[7] = "MinCardinality".length();
			
			i++;
		}
		
		//Set Width of Cells based on String Lenght
		setWidthForCell(excelSheet,maxLenght,Labels);
	}
	
	/**
	 * Generate sheet for MinCardinality Literal Relation
	 * 
	 * @throws WriteException 
	 */
	public void generateMinCardinalityLiteralSheet(LiteralAxiom MinCardinalityLiteralRelation, int NumSheet) throws WriteException
	{
		//Properties are in the sixth Sheet
		workbook.createSheet("MinCardinality (Literal)", NumSheet);
		WritableSheet excelSheet = workbook.getSheet(NumSheet);
		String [] Labels = {"Property", "Subject", "Relation", "Object", "URIProperty", "URISubject", "DataTypeObject", "Explanation - Extraction Rule"};
		createLabel(excelSheet, Labels);
		
		int i = 1; //Start write ad second line, after Labels
		int maxLenght[] = new int[Labels.length]; //Max length of content per cell
		
		for (List<RDFNode> row : MinCardinalityLiteralRelation.getLiteralAxiom()) {

			// First column
			addLabel(excelSheet, 0, i, ((Resource) row.get(1)).getLocalName());
			// Second column
			addLabel(excelSheet, 1, i, ((Resource) row.get(0)).getLocalName());
			// Third column
			addLabel(excelSheet, 2, i, ((Resource) row.get(1)).getLocalName());
			// Fourth column
			addLabel(excelSheet, 3, i, row.get(2).toString().substring(0, row.get(2).toString().indexOf("^^")));
			// Fifth column
			addLabel(excelSheet, 4, i, ((Resource) row.get(1)).getURI());
			// Sixth column
			addLabel(excelSheet, 5, i, ((Resource) row.get(0)).getURI());
			// Seventh column
			addLabel(excelSheet, 6, i, row.get(2).toString().substring(row.get(2).toString().indexOf("^^")+2, row.get(2).toString().length()));
			// Eighth column
			addLabel(excelSheet, 7, i, "MinCardinality Literal");
			
			
			//Update Max Length
			
			if( ((Resource) row.get(1)).getLocalName().length()>maxLenght[0] )
				maxLenght[0] = ((Resource) row.get(1)).getLocalName().length();
			
			if( ((Resource) row.get(0)).getLocalName().length()>maxLenght[1] )
				maxLenght[1] = ((Resource) row.get(0)).getLocalName().length();
			
			if( ((Resource) row.get(1)).getLocalName().length()>maxLenght[2] )
				maxLenght[2] = ((Resource) row.get(1)).getLocalName().length();
			
			if( row.get(2).toString().substring(0, row.get(2).toString().indexOf("^^")).length()>maxLenght[3] )
				maxLenght[3] = row.get(2).toString().substring(0, row.get(2).toString().indexOf("^^")).length();
			
			if( ((Resource) row.get(1)).getURI().length()>maxLenght[4] )
				maxLenght[4] = ((Resource) row.get(1)).getURI().length();
			
			if( ((Resource) row.get(0)).getURI().length()>maxLenght[5] )
				maxLenght[5] = ((Resource) row.get(0)).getURI().length();
			
			if( row.get(2).toString().substring(row.get(2).toString().indexOf("^^")+2, row.get(2).toString().length()).length()>maxLenght[6] )
				maxLenght[6] = row.get(2).toString().substring(row.get(2).toString().indexOf("^^")+2, row.get(2).toString().length()).length();
			
			maxLenght[7] = "MinCardinality Literal".length();
			
			i++;
		}
		
		//Set Width of Cells based on String Lenght
		setWidthForCell(excelSheet,maxLenght,Labels);
	}
	
	/**
	 * Generate sheet for Domain and Range Relation
	 * 
	 * @throws WriteException 
	 */
	public int generateDomainRangeSheet(DomainRange DRRelation, Properties allProperty, int NumSheet) throws WriteException
	{
		//Determino il numero di fogli da inserire per le propriet�
		HashMap<String, String> extractedProp = DRRelation.getPropertyType();
		HashMap<String,String> propType = new HashMap<String,String>();

		int NumSheetProp = 0; //Conta il numero di pagine da aggiungere per le propriet�
		
		for (String value : extractedProp.values()) {
			if(propType.get(value)==null) //Se ancora non � conteggiata
			{
				NumSheetProp++;
				propType.put(value,"");
			}
		}
		
		Object[] propTypeSet = propType.keySet().toArray();

		for(int kprop=0; kprop<NumSheetProp; kprop++){

			String currType = (String) propTypeSet[kprop];

			workbook.createSheet("Domain&Range-" + currType, NumSheet+kprop);
			WritableSheet excelSheet = workbook.getSheet(NumSheet+kprop);					

			//Filtro le propriet� in base al tipo corrente
			HashMap<String, ArrayList<OntResource>> extractedPropType = DRRelation.getDRRelation();
			Iterator<String> ePropTypeIt = extractedPropType.keySet().iterator();
			HashMap<String,ArrayList<OntResource>> typesForProperties = new HashMap<String,ArrayList<OntResource>>();

			while (ePropTypeIt.hasNext()) {
				String key = ePropTypeIt.next().toString();
				ArrayList<OntResource> value = DRRelation.getDRRelation().get(key);
				
				if(DRRelation.getPropertyType().get(key).toString().equals(currType)){
					typesForProperties.put(key, value);
				}
			}

			//Properties are in the seventh Sheet
			String [] Labels = {"Property", "Subject", "Relation", "Object", "URIProperty", "URISubject", "URIObject", "Explanation - Extraction Rule"};
			createLabel(excelSheet, Labels);

			int i = 1; //Start write ad second line, after Labels
			int maxLenght[] = new int[Labels.length]; //Max length of content per cell

			for(String property : typesForProperties.keySet()) {
				ArrayList<OntResource> types = typesForProperties.get(property);

				if(types.get(0).isAnon() || types.get(1).isAnon()) continue;
				
				// First column
				addLabel(excelSheet, 0, i, allProperty.getProperty().get(property));
				// Second column
				addLabel(excelSheet, 1, i, types.get(0).getLocalName());
				// Third column
				addLabel(excelSheet, 2, i, allProperty.getProperty().get(property));
				// Fourth column
				addLabel(excelSheet, 3, i, types.get(1).getLocalName());
				// Fifth column
				addLabel(excelSheet, 4, i, property);
				// Sixth column
				addLabel(excelSheet, 5, i, types.get(0).getURI());
				// Seventh column
				addLabel(excelSheet, 6, i, types.get(1).getURI());
				// Eighth column
				String ObtainedBy = DRRelation.getObtainedBy().get(property); //Get ObtainedBy info for Current Property
				if(ObtainedBy!=null && ObtainedBy.length()>0 && !ObtainedBy.equals("null"))
					addLabel(excelSheet, 7, i, ObtainedBy);
				else
					addLabel(excelSheet, 7, i, "-");

				//Update Max Length

				if( allProperty.getProperty().get(property).length()>maxLenght[0] )
					maxLenght[0] = allProperty.getProperty().get(property).length();

				if( types.get(0).getLocalName().length()>maxLenght[1] )
					maxLenght[1] = types.get(0).getLocalName().length();

				if( allProperty.getProperty().get(property).length()>maxLenght[2] )
					maxLenght[2] = allProperty.getProperty().get(property).length();

				if( types.get(1).getLocalName().length()>maxLenght[3] )
					maxLenght[3] = types.get(1).getLocalName().length();

				if( property.length()>maxLenght[4] )
					maxLenght[4] = property.length();

				if( types.get(0).getURI().length()>maxLenght[5] )
					maxLenght[5] = types.get(0).getURI().length();

				if( types.get(1).getURI().length()>maxLenght[6] )
					maxLenght[6] = types.get(1).getURI().length();

				if( ObtainedBy!=null && ObtainedBy.length()>maxLenght[7] )
					maxLenght[7] = ObtainedBy.length();

				i++;
			}

			//Set Width of Cells based on String Lenght
			setWidthForCell(excelSheet,maxLenght,Labels);
		}

		return NumSheet+NumSheetProp-1;
	}
	
	/**
	 * Generate sheet for Used Ontology
	 * 
	 * @throws WriteException 
	 */
	public void generateUsedOntologySheet(List<String> UsedOntology, int NumSheet) throws WriteException
	{
		//Concepts are in the first Sheet
		workbook.createSheet("UsedOntology", NumSheet);
		WritableSheet excelSheet = workbook.getSheet(NumSheet);
		String [] Labels = {"URI"};
		createLabel(excelSheet, Labels);

		int i = 1; //Start write ad second line, after Labels
		int maxLenght[] = new int[Labels.length]; //Max length of content per cell
		
		for(String usedOnt : UsedOntology){
			
			// First column
			addLabel(excelSheet, 0, i, usedOnt);
			//Update Max Length
			if( usedOnt.length()>maxLenght[0] )
				maxLenght[0] = usedOnt.length();
			
			i++;
		}
		
		//Set Width of Cells based on String Lenght
		setWidthForCell(excelSheet,maxLenght,Labels);
	}

	private void addCaption(WritableSheet sheet, int column, int row, String s)
			throws RowsExceededException, WriteException {
		Label label;
		label = new Label(column, row, s, timesBold);
		sheet.addCell(label);
	}

	private void addNumber(WritableSheet sheet, int column, int row,
			Integer integer) throws WriteException, RowsExceededException {
		Number number;
		number = new Number(column, row, integer, times);
		sheet.addCell(number);
	}

	private void addLabel(WritableSheet sheet, int column, int row, String s)
			throws WriteException, RowsExceededException {
		Label label;
		label = new Label(column, row, s, times);
		sheet.addCell(label);
	}
	
	//Set Width of Cells based on String Lenght
	private void setWidthForCell(WritableSheet excelSheet, int [] maxLenght, String [] Labels){
		for(int column=0; column<maxLenght.length; column++){
			//If size of content is 0, set width based on Label
			if(maxLenght[column]==0)
				maxLenght[column] = Labels[column].length();
			
			excelSheet.setColumnView(column, maxLenght[column]+10); //+10 for correction
		}
	}
}
