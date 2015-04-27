package it.unimib.disco.summarization.utility;

import it.unimib.disco.summarization.datatype.Concepts;
import it.unimib.disco.summarization.output.Events;

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

public class ComputeLongestPathHierarchy {

	DirectedGraph<String, DefaultEdge> completeGraph = new DefaultDirectedGraph<String, DefaultEdge>(DefaultEdge.class);
	HashMap<String,DirectedGraph<String, DefaultEdge>> longestPathHierarchy = new HashMap<String,DirectedGraph<String, DefaultEdge>>();
	HashMap<String,ArrayList<String>> longestPathLeaves = new HashMap<String,ArrayList<String>>();
	
	HashSet<String> listOfConcept = new HashSet<String>();
	HashSet<String> roots = new HashSet<String>();
	HashSet<String> leaves = new HashSet<String>();
	HashSet<String> subClassRelations = new HashSet<String>();
	
	String subClassOfFile;
	
	HashSet<String> concepts;
	
	Stack<String> path  = new Stack<String>();   // the current path
    Set<String> onPath  = new TreeSet<String>();     // the set of vertices on the path

	public ComputeLongestPathHierarchy(Concepts Concepts, String subClassOfFile) {
		this.subClassOfFile=subClassOfFile;
		this.concepts=cloneSet(Concepts.getConcepts().keySet());
	}
	
	public void computeLonghestPathHierarchy(String file){
		//Assume che non vi siano cicli, come dovrebbe essere per le ontologie
		
		buildConceptList(subClassOfFile, concepts);
		
		roots = cloneSet(listOfConcept); //Inizialmente assumo che tutti i concetti siano radici
		leaves = cloneSet(listOfConcept); //Inizialmente assumo che tutti i concetti siano foglie
		
		computeGraph();
		
		//Calcolo tutti i percorsi piu lunghi tra i concetti nella gerarchia

		Iterator<String> rootClasses = roots.iterator();

		new Events().info("Roots: " + roots.size());
		new Events().info("Leaves: " + leaves.size());
		
		while(rootClasses.hasNext()){
			
			String curRoot = rootClasses.next();
			
			new Events().info("processing " + curRoot);
			
			//Costruisco la struttura per salvare le informazioni sul percorso [Pu� essere ottimizzato]
			HashMap<String,Integer> posVertex = new HashMap<String,Integer>();
			//Contiene la provenienza dei nodi salvati nella struttura precedente
			HashMap<String,ArrayList<String>> provVertex = new HashMap<String,ArrayList<String>>();
			
			//Costruisco il grafo della gerarchia
			DirectedGraph<String, DefaultEdge> hierarchyGraph = new DefaultDirectedGraph<String, DefaultEdge>(DefaultEdge.class);
			
			//Salvo la radice
			hierarchyGraph.addVertex(curRoot);
			
			LinkedList<String> queue = new LinkedList<String>();
			
			//Inizializzo la coda con la radice
			queue.push(curRoot);
			posVertex.put(curRoot, 0);
			provVertex.put(curRoot, null);
			
			//Struttura che salver� le foglie di questa gerarchia
			ArrayList<String> leefHier = new ArrayList<String>();
			
			int processed = 0;
			
			while(!queue.isEmpty()){
				
				processed++;
				//Setto il vertice corrente di analisi
				String CurNode = queue.pollFirst();
				
				int depth_add = posVertex.get(CurNode)+1;
				
				//Scorro tutti i vertici adiacenti a questo
				Iterator<DefaultEdge> adjCurrVertexOutgoing = completeGraph.outgoingEdgesOf(CurNode).iterator();
				
				//E' una foglia, non ha archi in unscita
				if(!adjCurrVertexOutgoing.hasNext()){
					if(!leefHier.contains(CurNode)){
						leefHier.add(CurNode);
						new Events().info("found a leaf: " + CurNode + " on iteration " + processed);
					}
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
			longestPathLeaves.put(curRoot, leefHier);
			
		}
		
		Iterator<String> hierFromRoot = roots.iterator();
		
		try{
			// Create file 
			FileWriter fstream = new FileWriter(file);
			BufferedWriter out = new BufferedWriter(fstream);
			
			while (hierFromRoot.hasNext()){
				
				String root = hierFromRoot.next();
				
				//Prendo le foglie della gerarchia
				Iterator<String> leaves = longestPathLeaves.get(root).iterator();
				Stack<String> pathConc  = new Stack<String>();   // the current path
				
				while(leaves.hasNext()){ 
					AllPaths(longestPathHierarchy.get(root), root, leaves.next(), out, pathConc);
				}
				
			}
			out.close();
			
		}catch (Exception e){
			new Events().error("Error saving the paths file", e);
		}

	}

	private void buildConceptList(String subClassOfFile, HashSet<String> concepts){
		
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
				concepts.remove(subject);
				concepts.remove(object);
				
				listOfConcept.add(subject);
				listOfConcept.add(object);
				subClassRelations.add(strLine);
			}
			
			in.close();
		}catch (Exception e){
			new Events().error("Error processing creating the list of known concepts", e);
		}
		
		//Aggiungo eventuali concetti non in relazione di sottoclasse
		if(!concepts.isEmpty()){
			Iterator<String> listConcetti = concepts.iterator();
			while(listConcetti.hasNext()){
				listOfConcept.add(listConcetti.next());
			}
		}
	}
	

	private void computeGraph(){

		Iterator<String> relations = subClassRelations.iterator();

		while(relations.hasNext()){
			
			String relation = relations.next();
			
			//Memorizzo i concetti
			String[] subObj = relation.split("##");
			
			String subject = new String(subObj[0]);
			String object = new String(subObj[1]);
			
			//La libreria assicura l'assenza dell'introduzione di duplicati
			completeGraph.addVertex(subject);
			completeGraph.addVertex(object);
			
			completeGraph.addEdge(object, subject);
			
			//Rimuovo la classe sulla colonna (subject) dalle radici (E' sottoclasse di almeno una classe)
			roots.remove(subject);		
			
			//Rimuovo la classe sulla riga (object) dalle foglie (Ha almeno una sottoclasse)
			leaves.remove(object);		
		}
		
		//Aggiungo eventuali concetti non in relazione di sottoclasse al grafo
		if(!concepts.isEmpty()){

			Iterator<String> listConcetti = concepts.iterator();

			while(listConcetti.hasNext()){

				String conc = listConcetti.next();
				completeGraph.addVertex(conc);
				//Rimuovo la classe sulla riga (object) dalle foglie (E' una radice perch� non presente nella gerarchia)
				leaves.remove(conc);	
			}

		}

	}
	
	private HashSet<String> cloneSet(Set<String> Set) {
		HashSet<String> clonedSet = new HashSet<String>(Set.size());
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
}
