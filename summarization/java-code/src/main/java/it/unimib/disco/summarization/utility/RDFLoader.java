package it.unimib.disco.summarization.utility;

import java.io.File;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.sdb.SDBFactory;
import com.hp.hpl.jena.sdb.Store;
import com.hp.hpl.jena.sdb.StoreDesc;
import com.hp.hpl.jena.sdb.sql.SDBConnection;
import com.hp.hpl.jena.sdb.store.DatabaseType;
import com.hp.hpl.jena.sdb.store.LayoutType;
import com.hp.hpl.jena.util.FileManager;

public class RDFLoader {
	
	private File[] listOfFiles;
    
    public void loadRDFtoSDB(String datasetDir,String className,String DB_URL,String DB_USER,String DB_PASSWD,boolean create,boolean truncate){
    
        // create store description
        StoreDesc storeDesc = new StoreDesc(LayoutType.LayoutTripleNodesHash,DatabaseType.MySQL);
        
        // load database driver
        try {
            Class.forName(className);
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
        
        // create SDBConnection
        SDBConnection sdbConnection = new SDBConnection(DB_URL,DB_USER,DB_PASSWD);
        
        // connect to store
        Store store = SDBFactory.connectStore(sdbConnection,storeDesc);
        
        if(create)
        	store.getTableFormatter().create();
        
        if(truncate)
        	store.getTableFormatter().truncate();
        
        // connect store to dataset
        Dataset dataset = SDBFactory.connectDataset(store);
        
        
        //Load all RDF File in datasetDir
        File folder = new File(datasetDir);
        File[] listOfFiles = folder.listFiles();
        
        for( File RDF_FILE : listOfFiles)
        {
	        // prepare the model
	        Model tmpModel = ModelFactory.createDefaultModel();
	        System.out.println("FILE: " + RDF_FILE.getPath());
			FileManager.get().readModel(tmpModel,RDF_FILE.getPath(),"N3");
	        System.out.println("Loading: " + tmpModel.size() + " triple...\n");
	        
	        // add the model into the dataset
	        dataset.getDefaultModel().add(tmpModel);
        }
        
        // all done ... hopefully
        store.close();
        
    }
    
    public Model loadRDFtoModel(int currFile){
        
    	File RDF_FILE = listOfFiles[currFile];
    	
    	// prepare the model
    	Model tmpModel = ModelFactory.createDefaultModel();
    	System.out.println("FILE: " + RDF_FILE.getPath());
    	FileManager.get().readModel(tmpModel,RDF_FILE.getPath(),"N3");
    	System.out.println("Loading: " + tmpModel.size() + " triple...\n");
    	
    	return tmpModel;
    }
    
    public int getListOfFileSize(String datasetDir){
    	//Load all RDF File in datasetDir
        File folder = new File(datasetDir);
        listOfFiles = folder.listFiles();
        int listOfFilesSize = listOfFiles.length;
        
		return listOfFilesSize;
    }
    

    public void loadSPQRQLEndpointtoModel(String endpoint){

    	//TODO: implementare
    	/*
    	 * ESEMPIO QUERY, DA PERFEZIONARE PER FILTRARE TUTTI I DATI E USANDO LIMIT E OFFSET PER SUDDIVIDERE IL LAVORO IN BLOCCHI E IN THREAD
    	 * http://www.w3.org/TR/rdf-sparql-query/#modOffset
    	 * 
    	 * QueryExecution qexec = QueryExecutionFactory.create(query, ds) ;
	        try {
	        	ResultSet results = qexec.execSelect() ;

	            // ResultSetFormatter.out(results) ;

	        	for ( ; results.hasNext() ; )
	        	{
	        		QuerySolution soln = results.nextSolution() ;
	        		Resource x = soln.getResource("resource") ;       // Get a result variable by name.
	        		Resource r = soln.getResource("p") ; // Get a result variable - must be a resource
	        		Resource l = soln.getResource("o") ;   // Get a result variable - must be a literal

	        		System.out.println(x.hasProperty(RDF.type));
	        	}
	        } finally { qexec.close() ; }
    	 * 
    	 *  String query = "PREFIX rdf:<" + RDF.getURI() + ">" +
					   "PREFIX owl:<" + OWL.getURI() + ">" +
        			   "SELECT ?resource ?p ?o WHERE {" +
					   "?resource ?p ?o ." +
					   "OPTIONAL { ?resource2 a owl:Concept . FILTER(?resource2 = ?resource) }" +
					   "OPTIONAL { ?resource3 a owl:DatatypeProperty . FILTER(?resource3 = ?resource) } " +
					   "OPTIONAL { ?resource4 a rdf:Property . FILTER(?resource4 = ?resource) }" +
						  
					   "FILTER(!bound(?resource2))" +
					   "FILTER(!bound(?resource3))" +
					   "FILTER(!bound(?resource4))" +
					   "}";
    	 * 
    	 */
    }


}