package it.unimib.disco.summarization.starter;

import it.unimib.disco.summarization.datatype.Axiom;
import it.unimib.disco.summarization.datatype.Concept;
import it.unimib.disco.summarization.datatype.DomainRange;
import it.unimib.disco.summarization.datatype.EquConcept;
import it.unimib.disco.summarization.datatype.EquProperty;
import it.unimib.disco.summarization.datatype.InvProperty;
import it.unimib.disco.summarization.datatype.LiteralAxiom;
import it.unimib.disco.summarization.datatype.Property;
import it.unimib.disco.summarization.datatype.SubClassOf;
import it.unimib.disco.summarization.datatype.SubProperty;
import it.unimib.disco.summarization.extraction.ConceptExtractor;
import it.unimib.disco.summarization.extraction.EqConceptExtractor;
import it.unimib.disco.summarization.extraction.EqPropertyExtractor;
import it.unimib.disco.summarization.extraction.InvPropertyExtractor;
import it.unimib.disco.summarization.extraction.PropertyExtractor;
import it.unimib.disco.summarization.extraction.SubPropertyExtractor;
import it.unimib.disco.summarization.info.InfoExtractor;
import it.unimib.disco.summarization.relation.OntologyAxiomExtractor;
import it.unimib.disco.summarization.relation.OntologyDomainRangeExtractor;
import it.unimib.disco.summarization.relation.OntologySubclassOfExtractor;
import it.unimib.disco.summarization.utility.ComputeLongestPathHierarchy;
import it.unimib.disco.summarization.utility.CreateExcel;
import it.unimib.disco.summarization.utility.FileDataSupport;
import it.unimib.disco.summarization.utility.Model;
import it.unimib.disco.summarization.utility.UsedOntology;

import java.io.File;
import java.io.IOException;
import java.util.List;

import jxl.write.WriteException;

import com.hp.hpl.jena.ontology.OntModel;


/**
 * Open an Ontology and extract all the useful information
 *
 * @author Vincenzo Ferme
 */
public class Starter {

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		// Load File (Ontology or Dataset)
		// TODO: S - Read from directory or receive as command line input and Call extractor based on file type
		
		//Parametri
		String owlBaseFileArg = null;
		String reportDirectory = null;
		String datasetSupportFileDirectory = null;
		
		if (args.length == 3) {
			
			owlBaseFileArg=args[0];
			reportDirectory=args[1];
			datasetSupportFileDirectory=args[2];
			
		}
		else{
			System.err.println("Argument" + " must be 3 in this order: Ontology File Directory, Repor Directory (with / at the and), Dataset Computation Support Files Directory (with / at the and)");
		    System.exit(1);						
		}

		//ONTOLOGY
		
		//Ottengo il nome del file che rappresenta l'ontologia, con path assoluto per poter caricare il Model di Jena
		File folder = new File(owlBaseFileArg);
		File[] listOfFiles = folder.listFiles();
		String fileName = null;

		for (File file : listOfFiles) {
		    if (file.isFile()) {
		    	fileName = file.getName();
		  
			    String extension = "";
	
			    int i = fileName.lastIndexOf('.');
			    int p = Math.max(fileName.lastIndexOf('/'), fileName.lastIndexOf('\\'));
	
			    if (i > p) {
			        extension = fileName.substring(i+1);
			        
			        if(extension=="owl") //Ho trovato il file di mio interesse
			        	break;
			    }
		    }
		}
		
		if(fileName==null){ //L'ontologia non � presente, o non � nel formato corretto
			
			System.out.println("Ontology not found!");
			System.exit(1);
			
		}
		
		//Base File
		String owlBaseFile = "File:" + owlBaseFileArg + fileName;
		//String owlBaseFile = "File:Ontology/university.owl";

		//Model
		Model OntModel = new Model(null,owlBaseFile,"RDF/XML");
		OntModel ontologyModel = OntModel.getOntologyModel();
		
		//Extract Concept from Ontology Model
		ConceptExtractor cExtract = new ConceptExtractor();
		cExtract.setConcepts(ontologyModel);
		
		Concept Concepts = new Concept();
		Concepts.setConcepts(cExtract.getConcepts());
		Concepts.setExtractedConcepts(cExtract.getExtractedConcepts());
		Concepts.setObtainedBy(cExtract.getObtainedBy());
		
		//Extract Property from Ontology Model
		PropertyExtractor pExtract = new PropertyExtractor();
		pExtract.setProperty(ontologyModel);
		
		Property Properties = new Property();
		Properties.setProperty(pExtract.getProperty());
		Properties.setExtractedProperty(pExtract.getExtractedProperty());
		Properties.setCounter(pExtract.getCounter());
		
		//Extract SubProperty from Ontology Model
		SubPropertyExtractor spExtract = new SubPropertyExtractor();
		spExtract.setSubProperty(Properties);
		
		SubProperty subProperties = new SubProperty();
		subProperties.setExtractedSubProperty(spExtract.getExtractedSubProperty());
		subProperties.setCounter(spExtract.getCounter());
		
		//Extract InverseProperty from Ontology Model
		InvPropertyExtractor ipExtract = new InvPropertyExtractor();
		ipExtract.setInvProperty(Properties);
		
		InvProperty invProperties = new InvProperty();
		invProperties.setExtractedInvProperty(ipExtract.getExtractedInvProperty());
		invProperties.setCounter(ipExtract.getCounter());
		
		//Extract EquivalentProperty from Ontology Model
		EqPropertyExtractor epExtract = new EqPropertyExtractor();
		epExtract.setEquProperty(Properties, ontologyModel);
				
		EquProperty equProperties = new EquProperty();
		equProperties.setExtractedEquProperty(epExtract.getExtractedEquProperty());
		equProperties.setCounter(epExtract.getCounter());
		

		//Extract SubClassOf Relation from OntologyModel
		OntologySubclassOfExtractor SbExtractor = new OntologySubclassOfExtractor();
		//The Set of Concepts will be Updated if Superclasses are not in It
		SbExtractor.setConceptsSubclassOf(Concepts, ontologyModel);
		SubClassOf SubClassOfRelation = SbExtractor.getConceptsSubclassOf();
			
		
		//Extract Domain & Range Relation
		OntologyDomainRangeExtractor DRExtractor = new OntologyDomainRangeExtractor();
		//The Set of Concepts will be Updated if Domain or Range are not in It
		DRExtractor.setConceptsDomainRange(Concepts, Properties);
		DomainRange DRRelation = DRExtractor.getPropertyDomainRange();
		
		
		//Extract Axioms from OntologyModel
		OntologyAxiomExtractor SVFExtractor = new OntologyAxiomExtractor();
		//The Set of Concepts will be Updated if Superclasses are not in It
		SVFExtractor.setConceptsSomeValueFrom(Concepts, DRRelation, ontologyModel);
		SVFExtractor.setConceptsAllValueFrom(Concepts, DRRelation, ontologyModel);
		SVFExtractor.setConceptsMinCardinality(Concepts, DRRelation, ontologyModel);
		Axiom SomeValueFromRelation = SVFExtractor.getConceptsSomeValueFrom();
		Axiom AllValueFromRelation = SVFExtractor.getConceptsAllValueFrom();
		Axiom MinCardinalityRelation = SVFExtractor.getConceptsMinCardinality();
		LiteralAxiom MinCardinalityLiteralRelation = SVFExtractor.getConceptsMinCardinalityLiteral();
		LiteralAxiom SomeValueFromLiteralRelation = SVFExtractor.getConceptsSomeValueFromLiteral();
		LiteralAxiom AllValueFromLiteralRelation = SVFExtractor.getConceptsAllValueFromLiteral();
		
		
		//Extract EquivalentClass from Ontology Model - Qui per considerare tutti i concetti
		EqConceptExtractor equConcepts = new EqConceptExtractor();
		equConcepts.setEquConcept(Concepts, ontologyModel);
		
		EquConcept equConcept = new EquConcept();
		equConcept.setExtractedEquConcept(equConcepts.getExtractedEquConcept());
		equConcept.setEquConcept(equConcepts.getEquConcept());
		
		//Get List of Used Ontology
		List<String> usedOntology = UsedOntology.getUsedOntology(ontologyModel, Concepts, Properties, equProperties);
		
		//Extract Labels and Comments
		InfoExtractor info = new InfoExtractor();
		info.setConceptInfo(Concepts);
		info.setPropertyInfo(Properties);
		
		//Save Data in an Excel Report - TODO: Salvare su database
		
		//Pulisco i concetti da eventuali null e Thing
		Concepts.deleteThing();
		
		//Pulisco le relazioni di sottoclasse di Thing
		SubClassOfRelation.deleteThing();
		
		//TODO: Vedere se serve pulire anche gli altri elementi
		//
		// SomeValueFromRelation
		// AllValueFromRelation
		// MinCardinalityRelation
		// DRRelation
		
		//Write and Excel Report
		CreateExcel report = new CreateExcel();
		//report.setOutputFile("OutputTest/University/report.xls");
		report.setOutputFile(reportDirectory + "ontology.xls");
		
		//Write Down File
		
		try {
			//Create File to Write
			report.startWrite();
			
			//Write Data
			report.generateConceptsSheet(Concepts,0);
			report.generateEquConceptsSheet(equConcept,1);
			report.generateConceptsCountSheet(Concepts,2);
			report.generateConceptsLabelSheet(Concepts,info,3);
			report.generateConceptsCommentSheet(Concepts, info, 4);
			int numSheet = report.generatePropertiesSheet(Properties,5);
			report.generatePropertiesLabelSheet(Properties, info, numSheet+1);
			report.generatePropertiesCommentSheet(Properties, info, numSheet+2);			
			report.generateEquPropertiesSheet(equProperties,numSheet+3);
			report.generateSubPropertiesSheet(subProperties,numSheet+4);
			report.generateInvPropertiesSheet(invProperties,numSheet+5);
			report.generatePropertiesCountSheet(Properties,subProperties,invProperties,equProperties,numSheet+6);
			report.generateSubClassOfSheet(SubClassOfRelation,numSheet+7);
			report.generateSomeValueFromSheet(SomeValueFromRelation,numSheet+8);
			report.generateSomeValueFromLiteralSheet(SomeValueFromLiteralRelation,numSheet+9);
			report.generateAllValueFromSheet(AllValueFromRelation,numSheet+10);
			report.generateAllValueFromLiteralSheet(AllValueFromLiteralRelation,numSheet+11);
			report.generateMinCardinalitySheet(MinCardinalityRelation,numSheet+12);
			report.generateMinCardinalityLiteralSheet(MinCardinalityLiteralRelation,numSheet+13);
			numSheet = report.generateDomainRangeSheet(DRRelation, Properties,numSheet+14);
			report.generateUsedOntologySheet(usedOntology,numSheet+1);			
			//Close File and End Write
			report.endWrite();
		} catch (WriteException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	
		//DATASET - Il calcolo � fatto in awk
		
		//Load Triple into Datastore
		
		/* LOAD FROM DIRECTORY TO DATASTORE
		String dataset = "Dataset/DBPedia/en/"; //Le directory e I file non devono avere spazi nel nome
		String className = "com.mysql.jdbc.Driver";   
	    String DB_URL = "jdbc:mysql://127.0.0.1:3306/DBPedia";  
	    String DB_USER = "root";                          
	    String DB_PASSWD = "";
	    
		RDFLoader sd = new RDFLoader();
        sd.loadRDFtoSDB(dataset,className,DB_URL,DB_USER,DB_PASSWD,true,true);
		*/
		
		/*
		// LOAD RDF TO MODEL (ONE PER FILE) E ELABORA UN FILE ALLA VOLTA
		String dataset = "Dataset/DBPedia/en/"; //Le directory e I file non devono avere spazi nel nome
		RDFLoader sd = new RDFLoader();
        int numberOfFiles = sd.getListOfFileSize(dataset);
        
        */
        
        //Salvo le informazioni utilizzate per il calcolo dei percorsi nella gerarchia
        FileDataSupport writeFileSupp = new FileDataSupport(SubClassOfRelation, datasetSupportFileDirectory + "SubclassOf.txt", datasetSupportFileDirectory + "Concepts.txt", datasetSupportFileDirectory + "EquConcepts.txt", datasetSupportFileDirectory + "EquProperties.txt", datasetSupportFileDirectory + "DR.txt", datasetSupportFileDirectory + "Properties.txt", datasetSupportFileDirectory + "DTProperties.txt");
        writeFileSupp.writeSubclass(equConcept);
        
        //Calcolo tutti i percorsi nella gerarchia
        ComputeLongestPathHierarchy pathHierarchy = new ComputeLongestPathHierarchy(Concepts,datasetSupportFileDirectory + "SubclassOf.txt");
        pathHierarchy.computeLonghestPathHierarchy(datasetSupportFileDirectory + "path.txt",datasetSupportFileDirectory + "allSubConcept.txt");
        
        writeFileSupp.writeConcept(Concepts);
        writeFileSupp.writeEquclass(equConcept);
        writeFileSupp.writeEquProperty(equProperties);
        writeFileSupp.writeDR(DRRelation);
        writeFileSupp.writeProperty(Properties);
        
        //TODO: Sistemare in sezione opportuno per una corretta gestione all'interno del sistema
        
        // Map<String,String> conceptSnippet = new HashMap<String,String>();
        
        //GOOGLE:
        /*
        // Estrazione abstract/label/commento..
        for(Map.Entry<String,String> conc : Concepts.getConcepts().entrySet()){
        	
        	//System.out.println("Key: " + conc.getKey());
        	//System.out.println("Value: " + conc.getValue());
        	
        	Pattern pat = Pattern.compile("[A-Z]");
        	Matcher match = pat.matcher(conc.getValue());
        	StringBuffer stringToSearch = new StringBuffer();
        	
        	//Separo la stringa ogni lettera grande in quanto formalmente si tratta di parole distinte
        	//TODO: trovare modo generale, eventualmente poggiandosi a dizionario o altro per farlo indipendentemente dal formato iniziale nella ontologia
        	
        	int prevMatch = -1;
        	
        	while(match.find())
        	{
        		if(prevMatch!=-1)
        			stringToSearch.append(conc.getValue().substring(prevMatch, match.start()) + " "); // " " per google - %20
        		else
        			stringToSearch.append(conc.getValue().substring(0, match.start()));
        			
        		prevMatch = match.start();
        	}
        	
        	//System.out.println(prevMatch);
        	
        	//Aggiungo la parte finale della stringa
        	if(prevMatch!=-1 && prevMatch<conc.getValue().length()){
        		stringToSearch.append(conc.getValue().substring(prevMatch, conc.getValue().length()));
        	}
        	
        	if(prevMatch==-1){
        		stringToSearch.append(conc.getValue());
        	}
        	
        	//System.out.println("stringToSearch: " + stringToSearch);
        	
        	System.out.println(conc.getValue() + "-" + stringToSearch);
        	
        	// GOOGLE
        	String [] arguments = new String[1];
    		arguments[0] = stringToSearch + "  site:en.wikipedia.org";
    		
    		String firstSnippet = GoogleSearch.getFirstParagraphFromWikipediaLink(arguments);
    		
    		System.out.println(firstSnippet);
    		System.out.println();
    		
        	conceptSnippet.put(conc.getValue(), firstSnippet);
        	
        	try {
				Thread.sleep(15000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	
        	//WIKIPEDIA API
        	/*
        	URL apiCall = null;
        	
			try {
				//TODO: Separate ad Maiusc with %20
				apiCall = new URL("http://en.wikipedia.org/w/api.php?action=query&list=search&srsearch=" + stringToSearch + "&srprop=snippet&srlimit=10&srwhat=text&format=json");
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
            URLConnection apiCallConn = null;
            
			try {
				
				apiCallConn = apiCall.openConnection();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
			BufferedReader in = null;
            
			try {
				in = new BufferedReader(
				                        new InputStreamReader(
				                        apiCallConn.getInputStream()));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
            String inputLine;
            StringBuffer resp = new StringBuffer();

            try {
				while ((inputLine = in.readLine()) != null) 
					resp.append(inputLine);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
            try {
            	
				in.close();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
            System.out.println(resp);
            
            //TODO: Bisogna anche gestire casi di risposta vuota
            
            Object obj=JSONValue.parse(("[" + resp + "]").toString());
            JSONArray array=(JSONArray) obj;
                          
            JSONObject obj2=(JSONObject)array.get(0); 
            
            JSONObject obj3=(JSONObject) obj2.get("query");
            
            JSONArray obj4=(JSONArray) obj3.get("search");
            
            Iterator<?> obj4It = obj4.iterator();
            
            String firstTitle = null;
            String firstSnippet = null;
            boolean founded = false;
            //Con match title su primi N e prendo uguale se trovo, oppure primo o i primi (meglio primo, che in pratica � quello dove ridireziona dbpedia)
            while(obj4It.hasNext()){
            	
            	JSONObject obj5 = (JSONObject) obj4It.next();
            	
            	String title = (String) obj5.get("title");
            	String snippet =  (String) obj5.get("snippet");
            	
            	snippet = snippet.replaceAll("\\<.*?\\>", "");
            	//TODO: Rimuovere i puntini alla fine
            	
            	if(firstTitle==null)
            		firstTitle=title;
            	if(firstSnippet==null)
            		firstSnippet=snippet;
            		
            	if(title.equals(stringToSearch.toString().replace("%20", " "))){
            		conceptSnippet.put(conc.getValue(), snippet);
            		System.out.println(title);
            		System.out.println(snippet);
            		founded=true;
            		break;
            	}
            	
            }
            
            if(founded==false){
            	conceptSnippet.put(conc.getValue(), firstSnippet);
            }
         
            */
//        }
        /*
	
        try{
        	// Create file 
        	FileWriter fstream = new FileWriter("C:\\Users\\Vincenzo Ferme\\Downloads\\concsnip_google_first_package_wikipedia.txt");
        	BufferedWriter out = new BufferedWriter(fstream);

        	for(Map.Entry<String,String> concSnip : conceptSnippet.entrySet()){

        		out.write(concSnip.getKey()+";"+concSnip.getValue()+"\n");
        	}

        	//Close the output stream
        	out.close();

        }catch (Exception e){//Catch exception if any
        	System.err.println("Error: " + e.getMessage());
        }
        */
        
        //TODO: Riattivare se si vuol far calcolare da java, ma i metodi vanno allineati
        /*
        //DataExtractor dE = new DataExtractor(); 
        
        //dE.extractInfoFromData(Concepts, equProperties, equConcept, sd, numberOfFiles, ontologyModel); //Estraggo le informazioni dai dati
        
        //Creo il report contenente le informazioni estratte
        
        //TODO: Rimuovere
        try {
			//Create File to Write
			report.startWrite();
			
			//Write Data
			report.generateConceptsSheet(Concepts,0);
			report.generateEquConceptsSheet(equConcept,1);
			report.generateConceptsCountSheet(Concepts,2);
			report.generateConceptsLabelSheet(Concepts,info,3);
			report.generateConceptsCommentSheet(Concepts, info, 4);
			int numSheet = report.generatePropertiesSheet(Properties,5);
			report.generatePropertiesLabelSheet(Properties, info, numSheet+1);
			report.generatePropertiesCommentSheet(Properties, info, numSheet+2);			
			report.generateEquPropertiesSheet(equProperties,numSheet+3);
			report.generateSubPropertiesSheet(subProperties,numSheet+4);
			report.generateInvPropertiesSheet(invProperties,numSheet+5);
			report.generatePropertiesCountSheet(Properties,subProperties,invProperties,equProperties,numSheet+6);
			report.generateSubClassOfSheet(SubClassOfRelation,numSheet+7);
			report.generateSomeValueFromSheet(SomeValueFromRelation,numSheet+8);
			report.generateAllValueFromSheet(AllValueFromRelation,numSheet+9);
			report.generateMinCardinalitySheet(MinCardinalityRelation,numSheet+10);
			report.generateMinCardinalityLiteralSheet(MinCardinalityLiteralRelation,numSheet+11);
			numSheet = report.generateDomainRangeSheet(DRRelation, Properties,numSheet+12);
			report.generateUsedOntologySheet(usedOntology,numSheet+1);			
			//Close File and End Write
			report.endWrite();
		} catch (WriteException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		*/

	}

}
