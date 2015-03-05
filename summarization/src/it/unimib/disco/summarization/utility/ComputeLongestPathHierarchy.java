package it.unimib.disco.summarization.utility;

import it.unimib.disco.summarization.datatype.Concept;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;

import org.jgraph.graph.DefaultEdge;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;

//TODO: Rivedere e sistemare perchè può essere ottimizzato ( se serve ), ma sicuramente evitate alcune strutture

public class ComputeLongestPathHierarchy {

	DirectedGraph<String, DefaultEdge> completeGraph = new DefaultDirectedGraph<String, DefaultEdge>(DefaultEdge.class);
	HashMap<String,DirectedGraph<String, DefaultEdge>> longestPathHierarchy = new HashMap<String,DirectedGraph<String, DefaultEdge>>();
	HashMap<String,ArrayList<String>> longestPathLeef = new HashMap<String,ArrayList<String>>();
	private ArrayList<String> listOfConcept = new ArrayList<String>();
	ArrayList<String> listOfRoot = new ArrayList<String>();
	ArrayList<String> listOfLeef = new ArrayList<String>();
	ArrayList<String> listOfSubClassREl = new ArrayList<String>();
	int numOfConcept = 0;
	//int[][] adiacMatrix;
	String subClassOfFile;
	Set<String> Concepts;
	private Stack<String> path  = new Stack<String>();   // the current path
    private Set<String> onPath  = new TreeSet<String>();     // the set of vertices on the path

	public ComputeLongestPathHierarchy(Concept Concepts, String subClassOfFile) {
		this.subClassOfFile=subClassOfFile;
		this.Concepts=cloneSet(Concepts.getConcepts().keySet());
	}
	
	public void computeLonghestPathHierarchy(String file, String fileAllSubConcept){
		
		//Assume che non vi siano cicli, com'� per le ontologie
		
		getListOfConcept(subClassOfFile,Concepts);
		listOfRoot = cloneList(getListOfConcept()); //Inizialmente assumo che tutti i concetti siano radici
		listOfLeef = cloneList(getListOfConcept()); //Inizialmente assumo che tutti i concetti siano foglie
		computeGraph();
		
		//Calcolo tutti i percorsi pi� lunghi tra i concetti nella gerarchia

		Iterator<String> rootClasses = listOfRoot.iterator();

		while(rootClasses.hasNext()){
			
			String curRoot = rootClasses.next();
			
			//Costruisco la struttura per salvare le informazioni sul percorso [Pu� essere ottimizzato]
			HashMap<String,Integer> posVertex = new HashMap<String,Integer>();
			//Contiene la provenienza dei nodi salvati nella struttura precedente
			HashMap<String,ArrayList<String>> provVertex = new HashMap<String,ArrayList<String>>();
			
			//Costruisco il grafo della gerarchia
			DirectedGraph<String, DefaultEdge> hierarchyGraph = new DefaultDirectedGraph<String, DefaultEdge>(DefaultEdge.class);
			
			//Salvo la radice
			hierarchyGraph.addVertex(curRoot);
			
			int depth = 0; //Profondit� della navigazione del grafo
			
			LinkedList<String> queue = new LinkedList<String>();
			
			//Inizializzo la coda con la radice
			queue.push(curRoot);
			posVertex.put(curRoot, depth);
			provVertex.put(curRoot, null);
			
			//Struttura che salver� le foglie di questa gerarchia
			ArrayList<String> leefHier = new ArrayList<String>();
			
			while(!queue.isEmpty()){
				
				//Setto il vertice corrente di analisi
				String CurNode = queue.pollFirst();
				
				int depth_add = posVertex.get(CurNode)+1;
				
				//Scorro tutti i vertici adiacenti a questo
				Iterator<DefaultEdge> adjCurrVertexOutgoing = completeGraph.outgoingEdgesOf(CurNode).iterator();
				
				//E' una foglia, non ha archi in unscita
				if(!adjCurrVertexOutgoing.hasNext()){
					if(!leefHier.contains(CurNode))
						leefHier.add(CurNode);
				}
				
				while(adjCurrVertexOutgoing.hasNext()){
					
					String adjCurrVertex = completeGraph.getEdgeTarget(adjCurrVertexOutgoing.next());
					
					queue.push(adjCurrVertex); //Salvo il nodo per l'iterazione successiva, se non l'ho gi� visitato


					if(posVertex.get(adjCurrVertex)!=null){
						
						if( posVertex.get(adjCurrVertex) < depth_add){ //Rimuovo il percorso pi� breve e aggiorno a quello pi� lungo

							posVertex.put(adjCurrVertex, depth_add);
							
							//Rimuovo tutti gli archi in precedenza presenti
							Iterator<String> provVert = provVertex.get(adjCurrVertex).iterator();
							
							while(provVert.hasNext()){
								String toRem = provVert.next();
								hierarchyGraph.removeEdge(toRem, adjCurrVertex);
							}
							
							hierarchyGraph.addVertex(adjCurrVertex);
							
							ArrayList<String> prov = new ArrayList<String>();
							prov.add(CurNode);
							
							provVertex.put(adjCurrVertex, prov);
							hierarchyGraph.addEdge(CurNode, adjCurrVertex);
						}
						else if( posVertex.get(adjCurrVertex) == depth_add){ //Aggiungo il nuovo percorso della stessa lunghezza
							hierarchyGraph.addVertex(adjCurrVertex);
							
							ArrayList<String> prov = new ArrayList<String>();
							prov.add(CurNode);
							
							provVertex.put(adjCurrVertex, prov);
							hierarchyGraph.addEdge(CurNode, adjCurrVertex);
							
						}

					}
					else{ //Trovato la prima volta, va aggiunto
						posVertex.put(adjCurrVertex, depth_add);
						
						ArrayList<String> prov = new ArrayList<String>();
						prov.add(CurNode);
						
						provVertex.put(adjCurrVertex, prov);

						hierarchyGraph.addVertex(adjCurrVertex);
						hierarchyGraph.addEdge(CurNode, adjCurrVertex);
					}
				}
				
				
			}

			//Salvo l'albero della gerarchia
			longestPathHierarchy.put(curRoot, hierarchyGraph);
			
			//Salvo le foglie
			longestPathLeef.put(curRoot, leefHier);
			
		}
		
		Iterator<String> hierFromRoot = listOfRoot.iterator();
		
		try{
			// Create file 
			FileWriter fstream = new FileWriter(file);
			BufferedWriter out = new BufferedWriter(fstream);
			
			while (hierFromRoot.hasNext()){
				
				String currRoot = hierFromRoot.next();
				
				//Prendo le foglie della gerarchia
				Iterator<String> leefCurr = longestPathLeef.get(currRoot).iterator();
				Stack<String> pathConc  = new Stack<String>();   // the current path
				
				while(leefCurr.hasNext()){ 
					String foglia = leefCurr.next();
					
					AllPaths(longestPathHierarchy.get(currRoot), currRoot, foglia, out, pathConc);
				}
				
			}
			
			//Close the output stream
			out.close();
			//outConc.close();
			
		}catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}

	}

	private ArrayList<String> getListOfConcept(String subClassOfFile, Set<String> Concepts){
		
		//Leggo le relazioni di sottoclasse
		try{
			// Open the file that is the first 
			// command line parameter
			FileInputStream fstream = new FileInputStream(subClassOfFile);
			// Get the object of DataInputStream
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			//Read File Line By Line
			while ((strLine = br.readLine()) != null)   {
				
				//Memorizzo i concetti
				String[] subObj = strLine.split("##");
				
				String subject = subObj[0];
				String object = subObj[1];
				
				//Rimuovo dai concetti questi trovati, se presenti
				if(Concepts.contains(subject))
					Concepts.remove(subject);
				
				if(Concepts.contains(object))
					Concepts.remove(object);
				
				if(!listOfConcept.contains(subject)){
					listOfConcept.add(subject);
					numOfConcept++;
				}
				
				if(!listOfConcept.contains(object)){
					listOfConcept.add(object);
					numOfConcept++;
				}
				
				listOfSubClassREl.add(strLine);
				
			}
			//Close the input stream
			in.close();
		}catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
		
		//Aggiungo eventuali concetti non in relazione di sottoclasse
		if(!Concepts.isEmpty()){
			
			Iterator<String> listConcetti = Concepts.iterator();
			
			while(listConcetti.hasNext()){
				
				String conc = listConcetti.next();
				listOfConcept.add(conc);
				numOfConcept++;
			}
			
		}
		
		return null;
		
	}
	

	private void computeGraph(){

		//adiacMatrix = new int[numOfConcept][numOfConcept];

		Iterator<String> subClRel = listOfSubClassREl.iterator();

		while(subClRel.hasNext()){
			String Rel = subClRel.next();
			
			//Memorizzo i concetti
			String[] subObj = Rel.split("##");
			
			String subject = new String(subObj[0]);
			String object = new String(subObj[1]);
			
			//La libreria assicura l'assenza dell'introduzione di duplicati
			completeGraph.addVertex(subject);
			completeGraph.addVertex(object);
			
			completeGraph.addEdge(object, subject);
			
			//Rimuovo la classe sulla colonna (subject) dalle radici (E' sottoclasse di almeno una classe)
			listOfRoot.remove(subject);		
			
			//Rimuovo la classe sulla riga (object) dalle foglie (Ha almeno una sottoclasse)
			listOfLeef.remove(object);		
		}
		
		//Aggiungo eventuali concetti non in relazione di sottoclasse al grafo
		if(!Concepts.isEmpty()){

			Iterator<String> listConcetti = Concepts.iterator();

			while(listConcetti.hasNext()){

				String conc = listConcetti.next();
				completeGraph.addVertex(conc);
				//Rimuovo la classe sulla riga (object) dalle foglie (E' una radice perch� non presente nella gerarchia)
				listOfLeef.remove(conc);	
			}

		}

	}
	
	public static ArrayList<String> cloneList(ArrayList<String> List) {
		ArrayList<String> clonedList = new ArrayList<String>(List.size());
	    for (String conc : List) {
	        clonedList.add(new String(conc));
	    }
	    return clonedList;
	}
	
	public static Set<String> cloneSet(Set<String> Set) {
		Set<String> clonedSet = new HashSet<String>(Set.size());
	    for (String conc : Set) {
	        clonedSet.add(new String(conc));
	    }
	    return clonedSet;
	}
	
	private void AllPaths(DirectedGraph<String, DefaultEdge> G, String s, String t, BufferedWriter out, Stack<String> pathConc) throws IOException {
        enumerate(G, s, t, out, pathConc);
    }
	
	// use DFS
    private void enumerate(DirectedGraph<String, DefaultEdge> G, String v, String t, BufferedWriter out, Stack<String> pathConc) throws IOException {

        // add node v to current path from s
        path.push(v);
        if(!pathConc.contains(v))
        	pathConc.push(v);
        onPath.add(v);

        // found path from s to t - currently prints in reverse order because of stack
        if (v.equals(t))
        	out.write("" + path + "\n");

        // consider all neighbors that would continue path with repeating a node
        else {
        	
            for (DefaultEdge w : G.edgesOf(v)) {
            	String targetVert = G.getEdgeTarget(w).toString();
                if (!onPath.contains(targetVert)) enumerate(G, targetVert, t, out, pathConc);
            }
        }

        // done exploring from v, so remove from path
        path.pop();
        onPath.remove(v);
    }

	public ArrayList<String> getListOfConcept() {
		return listOfConcept;
	}

	public void setListOfConcept(ArrayList<String> listOfConcept) {
		this.listOfConcept = listOfConcept;
	}

}
