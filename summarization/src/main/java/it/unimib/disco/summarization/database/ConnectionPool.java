package it.unimib.disco.summarization.database;

import java.util.Vector;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 *  La classe che gestisce un pool di connessioni
 */
/**
 * The Class ConnectionPool.
 */
public class ConnectionPool {

	/*
	 * La variabile che gestisce l'unica istanza di ConnectionPool
	 * tale propriet� (il suo valore) si intender� come una propriet� di classe
	 * e non solo di una determinata istanza di essa
	 * 
	 */
	/** The connection pool. */
	private static ConnectionPool connectionPool = null;
	
	/** The free connections. */
	private Vector<Connection> freeConnections;  /* La coda di connessioni libere */
	
	/** The db url. */
	private String dbUrl;                        /* Il nome del database */
	
	/** The db login. */
	private String dbLogin;                      /* Il login per il database */
	
	/** The db password. */
	private String dbPassword;     	             /* La password di accesso al database*/
	
	/** The properties. */
	private Properties properties;               /* Lettura dei parametri*/
	
	/*
	 * Recupero il gestore dei log
	 */
	/** The log. */
	Logger log = Logger.getLogger(this.getClass().getName());
  

	/**
	 * Costruttore della classe ConnectionPool, privato per creare un
	 * singoletto.
	 * 
	 * @throws ConnectionPoolException
	 *             the connection pool exception
	 */
	private ConnectionPool() throws ConnectionPoolException 
	{
		/*
		 * Creo un nuovo oggetto per la lettura delle propriet�
		 */
	    try
	    {
	    	this.properties=new Properties();              
	    	this.properties.load(this.getClass().getClassLoader().getResourceAsStream("application.properties"));
	    }
	    catch (Exception e)
	    {
	    	log.log(Level.SEVERE,"Error " + this.getClass().getName() + ".loadParameters(): " + e.getMessage());
	    }

	    this.freeConnections = new Vector<Connection>();  /* Costruisce la coda delle connessioni libere*/
	    this.loadParameters();                            /* Carica i parametri per l'accesso alla base di dati*/
	    this.loadDriver();                                /* Carica il driver del database*/
	}

	
	/**
	 * Funzione privata della classe che ha lo scopo di caricare i parametri
	 * necessari alla connessione.
	 */
	private void loadParameters()
	{
	  try
	  {
		  /* stringa di connessione al database */
		  dbUrl = this.properties.getProperty("application.database.odbc_string");
		  
		  this.properties.getProperty("application.database.provider");
    
		  /*username per l'accesso al database*/
		  dbLogin = this.properties.getProperty("application.database.username");                       
		  
		  /*password per l'accesso al database*/
		  dbPassword = this.properties.getProperty("application.database.password");           
		  
	  }
	  catch (Exception e)
	  {
		  log.log(Level.SEVERE,"Error " + this.getClass().getName() + ".loadParameters(): " + e.getMessage());
	  }
	}

	
	/**
	 * Funzione privata che carica il driver per l'accesso al database.
	 * In caso di errore durante il caricamento del driver solleva un'eccezione.
	 * 
	 * @throws ConnectionPoolException the connection pool exception
	 */
	private void loadDriver() throws ConnectionPoolException 
	{
		try
		{
			java.lang.Class.forName(this.properties.getProperty("application.database.provider"));
		}
		catch (Exception e)
		{
			throw new ConnectionPoolException();
		}
	}

  
	/**
	 * Funzione pubblica restituisce un pool di connessioni
	 * 
	 * Nota:
	 * Viene usata la parola riservata synchronized consente una accurata gestione dei metodi
	 * che possono essere richiamati contemporaneamente da pi� thread,
	 * quando si presenti la necessit� di garantire un accesso sincronizzato ad
	 * eventuali risorse gestite da tali metodi.
	 * 
	 * @return il pool di connessioni
	 * 
	 * @throws ConnectionPoolException the connection pool exception
	 */
	public static synchronized ConnectionPool getConnectionPool() throws ConnectionPoolException 
	{
		if(connectionPool == null) 
		{
			connectionPool = new ConnectionPool();
		}

		return connectionPool;
	}

  
	/**
	 * Funzione pubblica che restituisce una connessione libera prelevandola
	 * dalla coda freeConnections oppure se non ci sono connessioni disponibili
	 * creandone una nuova con una chiamata a newConnection.
	 * 
	 * Nota:
	 * Usiamo la coda perch� cos� prendiamo la connessione pi� vecchia fra quelle in cache,
	 * minimizzando la possibilit� di caduta per le connessioni inutilizzate da troppo tempo.
	 * 
	 * @return la connessione
	 * 
	 * @throws ConnectionPoolException the connection pool exception
	 */
	public synchronized Connection getConnection() throws ConnectionPoolException 
	{
		Connection connessione;

		if(freeConnections.size() > 0) 
		{
			/* Se la coda delle connessioni libere non � vuota */
			connessione = (Connection)freeConnections.firstElement();  /*Preleva il primo elemento*/
			freeConnections.removeElementAt(0);                        /*e lo cancella dalla coda*/

			try 
			{
				/* Verifica se la connessione non � pi� valida */
				if(connessione.isClosed()) 
				{          
					connessione = getConnection();    /* Richiama getConnection ricorsivamente*/
				}
			}
			catch(SQLException e) /* Se viene sollevata un eccezione*/
			{   
				connessione = getConnection();        /* Richiama getConnection ricorsivamente*/  
			}
		}
		else /* se la coda delle connessioni libere � vuota*/
		{                                
			connessione = newConnection();            /*crea una nuova connessione*/
		}
		return connessione;                           /* restituisce una connessione valida*/
	} 
    

	/**
	 * Funzione privata restituisce una nuova connessione
	 * 
	 * Nota: Usiamo la coda perch� cos� prendiamo la connessione pi� vecchia fra
	 * quelle in cache, minimizzando la possibilit� di caduta per le connessioni
	 * inutilizzate da troppo tempo.
	 * 
	 * @return the connection
	 * @throws ConnectionPoolException
	 *             the connection pool exception
	 */

	private Connection newConnection() throws ConnectionPoolException 
	{
		Connection connessione = null;

		try 
		{
			/*
			 * crea la connessione
			 */
			connessione = DriverManager.getConnection(this.dbUrl,this.dbLogin,this.dbPassword); 
			
		}
		catch(SQLException e)
		{                      
			throw new ConnectionPoolException();       /*  solleva un'eccezione in caso di errore */
		}

		/*restituisce la nuova connessione*/
		return connessione;                                  
	}

  
	/**
	 * Funzione pubblica che rilascia una connessione inserendola nella coda
	 * delle connessioni libere.
	 * 
	 * @param con
	 *            the con
	 */
	public synchronized void releaseConnection(Connection con) 
	{
		/*
		 * Inserisce la connessione alla fine della coda
		 */
		freeConnections.add(con);
	}

}

    
