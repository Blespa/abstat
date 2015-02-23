/**
 * Questa classe presenta una serie di funzioni
 * allo scopo di rendere la gestione del database pi�
 * semplice.
 * 
 * @version 1.00
 * @author Gruppo Tec. Web
 * 
 */
package it.unimib.disco.summarization.database;

/* libreria per l'accesso ai dati */
import java.sql.Connection;
import java.sql.SQLException;
/* libreria usata per l'arraylist */
import java.util.ArrayList;
/* librerie usate per log4j       */
import java.util.logging.Level;
import java.util.logging.Logger;
/* 
 * libreria utilizzata per la gestione dei prepared statement
 * e la gestione dei dati prelevati da database
 */
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * The Class WorkingOnDB.
 */
public class WorkingOnDB {

	/*Rendo impossibile creare istanze di questa classe*/
	/**
	 * Instantiates a new working on db.
	 */
	private WorkingOnDB() {}


	/**
	 * Questa funzione inserisce un record in una certa tabella
	 * specificata dal parametro table_name.
	 * 
	 * Esempio di utilizzo:
	 * 
	 * ArrayList<DbFields> Fileds= new ArrayList<DbFields> ();
	 * Fileds.add(new DbFields("dec1","descrizione1.1",DbFields.Type.String));
	 * Fileds.add(new DbFields("dec2","descrizione2.2",DbFields.Type.String));
	 * Fileds.add(new DbFields("intero","122",DbFields.Type.Int));
	 * 
	 * WorkingOnDB.Insert("tbltest", Fileds);
	 * 
	 * @param table_name nome della tabella
	 * @param fileds ArrayList con i campi della tabella
	 * 
	 * @see it.tecweb.database
	 */
	public static void Insert(String table_name, ArrayList<DbFields> fileds)
	{
		/*
		 * oggetto connection
		 */
		Connection connessione = null;
		/*
		 * oggetto PreparedStatement
		 */
		PreparedStatement prepStat = null;
		/*
		 * punta ad un campo (oggetto della tabella DbFields)
		 */
		DbFields field;
		/*
		 * indice usato per i for
		 */
		int i;
		

		/*
		 * check dei valori passati in ingresso
		 */
		if (table_name.length()<=0)
		{
			System.out.println("Errore nella funzione WorkingOnDB.Insert campo table_name nullo! ");
		}
		else
		{
	        try
	        {
	        	connessione = ConnectionPool.getConnectionPool().getConnection();
	        	
		        /*
		         * Crea un oggetto Statement
		         */
		        String sql = "INSERT INTO " + table_name + " ( " ;
		        
	        	/*
	        	 * scorro i campi della tabella
	        	 */
	        	for (i=0;i<fileds.size();i++)
	        	{
	        		if (i==fileds.size()-1)
	        		{
	        			sql += fileds.get(i).getField().toString() + " ) ";
	        		}
	        		else
	        		{
	        			sql += fileds.get(i).getField().toString() + " , ";
	        		}
	        	}
	        	
	        	sql += " VALUES ( " ;
	        	
	        	/*
	        	 * inserisco i ?
	        	 */
	        	for (i=0;i<fileds.size();i++)
	        	{
	        		if (i==fileds.size()-1)
	        		{
	        			sql += " ? ) ";
	        		}
	        		else
	        		{
	        			sql += " ? , ";
	        		}
	        	}
	        	
		        prepStat = (PreparedStatement) connessione.prepareStatement(sql);
		        
		        /*
		         * compilo con i valori,
		         * scorro i campi della tabella
		         */
	        	for (i=0;i<fileds.size();i++)
	        	{
	        		field=(DbFields)fileds.get(i);
	        		
	        		if (field.getType()==DbFields.Type.String)
	        		{
	        			prepStat.setString(i+1, field.getValue());	
	        		}
	        		else if (field.getType()==DbFields.Type.Date || field.getType()==DbFields.Type.DateTime)
	        		{
	        			prepStat.setString(i+1, field.getValue());
	        		}
	        		else if (field.getType()==DbFields.Type.Bool)
	        		{
	        			prepStat.setBoolean(i+1, Boolean.getBoolean(field.getValue()));
	        		}
	        		else if (field.getType()==DbFields.Type.Int)
	        		{
	        			int value = Integer.parseInt(field.getValue().trim());
	        			prepStat.setInt(i+1, value);
	        		}
	        	}

	        	prepStat.executeUpdate();

		        /*
		         * rilascio la connessione nel pool
		         */
		        ConnectionPool.getConnectionPool().releaseConnection(connessione);
		        
	        }
	        catch(Exception e) 
	        {
	        	System.out.println(e.getMessage());
	        }
	        
		}
	}
	
	/**
	 * Questa funzione effettua l'inserimento di un record in una certa tabella
	 * specificata dal parametro table_name.
	 * Esempio di utilizzo:
	 * 
	 * WorkingOnDB.Insert("INSERT tbltest() VALUES()");
	 * 
	 * @param sql la query sql
	 * 
	 * @see it.tecweb.database
	 */
	public static void Insert(String sql)
	{
		/*
		 * oggetto connection
		 */
		Connection con = null;
		/*
		 * oggetto PreparedStatement
		 */
		PreparedStatement prepStat = null;

        try

        {
        	/* recupero una connessione dall'pool */
        	con = ConnectionPool.getConnectionPool().getConnection();
	        	
        	/* preparo l'oggetto PreparedStatement */
	        prepStat = (PreparedStatement) con.prepareStatement(sql);
	        

        	/*
        	 * Lancio l'esecuzione della query
        	 */
        	prepStat.executeUpdate();
        	
	        /*
	         * rilascio la connessione nel pool
	         */
        	ConnectionPool.getConnectionPool().releaseConnection(con);
        }
        catch(Exception e) 
        {
        	System.out.println(e.getMessage());
        }
	}
	
	/**
	 * Questa funzione effettua l'update di un record in una certa tabella
	 * specificata dal parametro table_name.
	 * Esempio di utilizzo:
	 * 
	 * ArrayList<DbFields> Fileds= new ArrayList<DbFields> ();
	 * Fileds.add(new DbFields("dec1","descrizione1.1",DbFields.Type.String));
	 * Fileds.add(new DbFields("dec2","descrizione2.2",DbFields.Type.String));
	 * Fileds.add(new DbFields("intero","122",DbFields.Type.Int));
	 * 
	 * ArrayList<DbFields> Condictions= new ArrayList<DbFields> ();
	 * Condictions.add(new DbFields("intero","12",DbFields.Type.Int));
	 * Condictions.add(new DbFields("dec1","descrizione1.1",DbFields.Type.String));
	 * 
	 * WorkingOnDB.Update("tbltest", Fileds,Condictions);
	 * 
	 * @param table_name nome della tabella
	 * @param fileds ArrayList con i campi della tabella
	 * @param condictions l'array delle condizioni per la keyword
	 * 
	 * @see it.tecweb.database
	 */
	public static void Update(String table_name, ArrayList<DbFields> fileds, ArrayList<DbFields> condictions)
	{
		/*
		 * oggetto connection
		 */
		Connection connessione = null;
		/*
		 * oggetto PreparedStatement
		 */
		PreparedStatement prepStat = null;
		/*
		 * punta ad un campo (oggetto della tabella DbFields)
		 */
		DbFields field;
		/*
		 * indice usato per i for
		 */
		int i;
		/*
		 * indice per contare i parametri passati nell'ArrayList
		 */
		int indice_parametri=0;
		

		/*
		 * check dei valori passati in ingresso
		 */
		if (table_name.length()<=0)
		{
			System.out.println("Errore nella funzione WorkingOnDB.Update campo table_name nullo! ");
		}
		else
		{
	        try
	        {
	        	/*
	        	 * recupero una connessione dall'pool
	        	 */
	        	connessione = ConnectionPool.getConnectionPool().getConnection();

	        	
		        /*
		         *  costruisco la query per l'update in base ai 
		         *  parametri passati dall'utente
		         */
		        String sql = "UPDATE " + table_name + " SET " ;
		        
	        	/*
	        	 * scorro i campi della tabella
	        	 */
	        	for (i=0;i<fileds.size();i++)
	        	{
	        		if (i==fileds.size()-1)
	        		{
	        			sql += fileds.get(i).getField().toString() + " = ? ";
	        		}
	        		else
	        		{
	        			sql += fileds.get(i).getField().toString() + " = ? , ";
	        		}
	        	}

	        	
	        	/*
	        	 * inserisco le condizioni per il filtro
	        	 */
	        	for (i=0;i<condictions.size();i++)
	        	{
	        		if (i==0)
	        		{
	        			sql += " WHERE  " + condictions.get(i).getField().toString() + " = ?";
	        		}
	        		else
	        		{
	        			sql += " AND " + condictions.get(i).getField().toString() + " = ? ";
	        		}
	        	}

	        	/*
	        	 * preparo l'oggetto PreparedStatement
	        	 */
		        prepStat = (PreparedStatement) connessione.prepareStatement(sql);
		        
		        /*
		         * compilo con i valori
		         * scorro i campi della tabella
		         * 
		         */
	        	for (i=0;i<fileds.size();i++)
	        	{
	        		field=(DbFields)fileds.get(i);
	        	
	        		if (field.getType()==DbFields.Type.String)
	        		{
	        			prepStat.setString(i+1, field.getValue());	
	        		}
	        		else if (field.getType()==DbFields.Type.Date || field.getType()==DbFields.Type.DateTime)
	        		{
	        			prepStat.setString(i+1, field.getValue());
	        		}
	        		else if (field.getType()==DbFields.Type.Bool)
	        		{
	        			prepStat.setBoolean(i+1, Boolean.getBoolean(field.getValue()));
	        		}
	        		else if (field.getType()==DbFields.Type.Int)
	        		{
	        			int value = Integer.parseInt(field.getValue().trim());
	        			prepStat.setInt(i+1, value);
	        		}
	        		
	        	}
	        	
        		/*
        		 * aggiungo le condizioni
        		 */
	        	indice_parametri=fileds.size();
	        	
	        	for (i=0;i<condictions.size();i++)
	        	{
	        		indice_parametri++;
	        		field=(DbFields)condictions.get(i);
	        		if (field.getType()==DbFields.Type.String)
	        		{
	        			prepStat.setString(indice_parametri, field.getValue());	
	        		}
	        		else if (field.getType()==DbFields.Type.Date || field.getType()==DbFields.Type.DateTime)
	        		{
	        			prepStat.setString(indice_parametri, field.getValue());
	        		}
	        		else if (field.getType()==DbFields.Type.Bool)
	        		{
	        			prepStat.setBoolean(indice_parametri, Boolean.getBoolean(field.getValue()));
	        		}
	        		else if (field.getType()==DbFields.Type.Int)
	        		{
	        			int value = Integer.parseInt(field.getValue().trim());
	        			prepStat.setInt(indice_parametri, value);
	        		}
	        	}

	        	/*
	        	 * Lancio l'esecuzione della query
	        	 */
	        	prepStat.executeUpdate();

		        /*
		         * rilascio la connessione nel pool
		         */
		        ConnectionPool.getConnectionPool().releaseConnection(connessione);	        
	        }
	        catch(Exception e)
	        {
	        	System.out.println(e.getMessage());
	        }
	        
		}
	}

    /**
     * Questa funzione effettua l'update di un record in una certa tabella
     * specificata dal parametro table_name.
     * Esempio di utilizzo:
     * 
     * WorkingOnDB.Update("UPDATE tbltest SET A=B");
     * 
     * @param sql la query sql
     * 
     * @return numero di righe modificate
     * 
     * @see it.tecweb.database
     */
	public static int Update(String sql)
	{
		/*
		 * oggetto connection
		 */
		Connection con = null;
		/*
		 * oggetto PreparedStatement
		 */
		PreparedStatement prepStat = null;
		
		/* Conterr� il numero di righe modificate*/
		int modify = 0;

        try

        {
        	/* recupero una connessione dall'pool */
        	con = ConnectionPool.getConnectionPool().getConnection();
	        	
        	/* preparo l'oggetto PreparedStatement */
	        prepStat = (PreparedStatement) con.prepareStatement(sql);
	        

        	/*
        	 * Lancio l'esecuzione della query
        	 */
        	modify=prepStat.executeUpdate();
        	
	        /*
	         * rilascio la connessione nel pool
	         */
        	ConnectionPool.getConnectionPool().releaseConnection(con);
        }
        catch(Exception e) 
        {
        	System.out.println(e.getMessage());
        }
        
        return modify;
	}
	
	/**
	 * Questa funzione effettua la cancellazione FISICA di un record in una certa tabella
	 * specificata dal parametro table_name.
	 * 
	 * Esempio di utilizzo:
	 * 
	 * ArrayList<DbFields> Condictions= new ArrayList<DbFields> ();
	 * Condictions.add(new DbFields("intero","12",DbFields.Type.Int));
	 * Condictions.add(new DbFields("dec1","descrizione1.1",DbFields.Type.String));
	 * 
	 * WorkingOnDB.Delete("tbltest", Condictions);
	 * 
	 * @param table_name nome della tabella
	 * @param condictions l'array delle condizioni per la keyword
	 * 
	 * @see it.tecweb.database
	 */
	public static void Delete(String table_name, ArrayList<DbFields> condictions)
	{
		/*
		 * oggetto connection
		 */
		Connection connessione = null;
		/*
		 * oggetto PreparedStatement
		 */
		PreparedStatement prepStat = null;
		/*
		 * punta ad un campo (oggetto della tabella DbFields)
		 */
		DbFields field;
		/*
		 * indice usato per i for
		 */
		int i;
		

		/*
		 * check dei valori passati in ingresso
		 */
		if (table_name.length()<=0)
		{
			System.out.println("Errore nella funzione WorkingOnDB.DeleteReal campo table_name nullo! ");
		}
		else
		{
	        try
	        {
	        	/*
	        	 * recupero una connessione dall'pool
	        	 */
	        	connessione = ConnectionPool.getConnectionPool().getConnection();

	        	
		        /*
		         *  costruisco la query per l'update in base ai 
		         *  parametri passati dall'utente
		         */
		        String sql = "DELETE FROM " + table_name + " ";
		        
	        	/*
	        	 * inserisco le condizioni per il filtro
	        	 */
	        	for (i=0;i<condictions.size();i++)
	        	{
	        		if (i==0)
	        		{
	        			sql += " WHERE  " + condictions.get(i).getField().toString() + " = ?";
	        		}
	        		else
	        		{
	        			sql += " AND " + condictions.get(i).getField().toString() + " = ? ";
	        		}
	        	}

	        	/*
	        	 * preparo l'oggetto PreparedStatement
	        	 */
		        prepStat = (PreparedStatement) connessione.prepareStatement(sql);
	        	
        		/*
        		 * aggiungo le condizioni
        		 */
	        	for (i=0;i<condictions.size();i++)
	        	{
	        		field=(DbFields)condictions.get(i);
	        		if (field.getType()==DbFields.Type.String)
	        		{
	        			prepStat.setString(i+1, field.getValue());	
	        		}
	        		else if (field.getType()==DbFields.Type.Date || field.getType()==DbFields.Type.DateTime)
	        		{
	        			prepStat.setString(i+1, field.getValue());
	        		}
	        		else if (field.getType()==DbFields.Type.Bool)
	        		{
	        			prepStat.setBoolean(i+1, Boolean.getBoolean(field.getValue()));
	        		}
	        		else if (field.getType()==DbFields.Type.Int)
	        		{
	        			int value = Integer.parseInt(field.getValue().trim());
	        			prepStat.setInt(i+1, value);
	        		}
	        	}

	        	/*
	        	 * Lancio l'esecuzione della query
	        	 */
	        	prepStat.executeUpdate();

		        /*
		         * rilascio la connessione nel pool
		         */
		        ConnectionPool.getConnectionPool().releaseConnection(connessione);        
	        }
	        catch(Exception e)
	        {
	        	System.out.println(e.getMessage());
	        }
		}
	}
    
    /**
     * Questa funzione restituisce un TRUE o FALSE a seconda che la query eseguita
     * � vuota o meno.
     * 
     * Esempi di utilizzo:
     * 
     * ArrayList<DbFields> Condictions= new ArrayList<DbFields> ();
     * Condictions.add(new DbFields("intero","12",DbFields.Type.Int));
     * Condictions.add(new DbFields("dec1","descrizione1.1",DbFields.Type.String));
     * 
     * WorkingOnDB.IsEmpty("tbltest", Condictions);
     * 
     * oppure nel caso in cui ci sono parametri
     * WorkingOnDB.IsEmpty("tbltest",new ArrayList<DbFields>());
     * 
     * @param table_name nome della tabella
     * @param condictions l'array delle condizioni per la keyword
     * 
     * @return vero se il recordset � vuoto
     * 
     * @see it.tecweb.database
     */
	public static boolean IsEmpty(String table_name, ArrayList<DbFields> condictions)
	{
		/*
		 * oggetto connection
		 */
		Connection con = null;
		/*
		 * oggetto PreparedStatement
		 */
		PreparedStatement prepStat = null;
		/*
		 * punta ad un campo (oggetto della tabella DbFields)
		 */
		DbFields field;
		/*
		 * indice usato per i for
		 */
		int i;
		
		/*
		 * variabile di ritorno
		 */
		boolean ritorno_funzione=false;
		

		/*
		 * check dei valori passati in ingresso
		 */
		if (table_name.length()<=0)
		{
			System.out.println("Errore nella funzione WorkingOnDB.DeleteReal campo table_name nullo! ");
		}
		else
		{
	        try
	        {
	        	/*
	        	 * recupero una connessione dall'pool
	        	 */
	        	con = ConnectionPool.getConnectionPool().getConnection();

	        	
		        /*
		         *  costruisco la query per l'update in base ai 
		         *  parametri passati dall'utente
		         */
		        String sql = "Select * from " + table_name + " ";
		        
	        	/*
	        	 * inserisco le condizioni per il filtro
	        	 */
	        	for (i=0;i<condictions.size();i++)
	        	{
	        		if (i==0)
	        		{
	        			sql += " WHERE  " + condictions.get(i).getField().toString() + " = ?";
	        		}
	        		else
	        		{
	        			sql += " AND " + condictions.get(i).getField().toString() + " = ? ";
	        		}
	        	}

	        	/*
	        	 * preparo l'oggetto PreparedStatement
	        	 */
		        prepStat = (PreparedStatement) con.prepareStatement(sql);
		        
	        	
        		/*
        		 * aggiungo le condizioni
        		 */
	        	for (i=0;i<condictions.size();i++)
	        	{
	        		field=(DbFields)condictions.get(i);
	        		if (field.getType()==DbFields.Type.String)
	        		{
	        			prepStat.setString(i+1, field.getValue());	
	        		}
	        		else if (field.getType()==DbFields.Type.Date || field.getType()==DbFields.Type.DateTime)
	        		{
	        			prepStat.setString(i+1, field.getValue());
	        		}
	        		else if (field.getType()==DbFields.Type.Bool)
	        		{
	        			prepStat.setBoolean(i+1, Boolean.getBoolean(field.getValue()));
	        		}
	        		else if (field.getType()==DbFields.Type.Int)
	        		{
	        			int value = Integer.parseInt(field.getValue().trim());
	        			prepStat.setInt(i+1, value);
	        		}
	        	}

	        	/*
	        	 * Lancio l'esecuzione della query e prendo il risultato
	        	 */
	        	ResultSet rs = (ResultSet) prepStat.executeQuery();
		        while(rs.next() && ritorno_funzione==false) 
		        {
		        	ritorno_funzione=true;
		        }


		        /*
		         * rilascio la connessione nel pool
		         */
		        ConnectionPool.getConnectionPool().releaseConnection(con);  
	        }
	        catch(Exception e) 
	        {
	        	System.out.println(e.getMessage());
	        	ritorno_funzione=false;
	        }
	        
		}
		
		return !ritorno_funzione;
	}
	

	
	/**
	 * Questa funzione restituisce un TRUE o FALSE a seconda che la query eseguita
	 * � vuoto o meno.
	 * 
	 * Esempi di utilizzo:
	 * 
	 * WorkingOnDB.IsEmpty("SELECT * FROM Tabella WHERE id=123");
	 * 
	 * oppure nel caso in cui ci sono parametri
	 * WorkingOnDB.IsEmpty("tbltest",new ArrayList<DbFields>());
	 * 
	 * @param sql la query sql
	 * 
	 * @return vero se il recordset � vuoto
	 * 
	 * @see it.tecweb.database
	 */
	public static boolean IsEmpty(String sql)
	{
		/*
		 * oggetto connection
		 */
		Connection con = null;
		/*
		 * oggetto PreparedStatement
		 */
		PreparedStatement prepStat = null;
		/*
		 * variabile di ritorno
		 */
		boolean ritorno_funzione=false;
		

        try
        {
	       	/*
	       	 * recupero una connessione dall'pool
	       	 */
        	con = ConnectionPool.getConnectionPool().getConnection();

	       	/*
	       	 * costruisco la query per l'update in base ai 
	       	 * parametri passati dall'utente 
	       	 */
	       	prepStat = (PreparedStatement) con.prepareStatement(sql);

	       	/* Lancio l'esecuzione della query e prendo il risultato */
	       	ResultSet rs = (ResultSet) prepStat.executeQuery();
	       	while(rs.next() && ritorno_funzione==false)

	       	{
	       		ritorno_funzione=true;
	       	}

	       	/* rilascio la connessione nel pool */
	       	ConnectionPool.getConnectionPool().releaseConnection(con);  
        }
        catch(Exception e) 
        {
        	System.out.println(e.getMessage());
        	ritorno_funzione=false;
        }
		
		return !ritorno_funzione;
	}
	
	/**
	 * Questa funzione restituisce un oggetto ResultSet con i valori dei campi
	 * letti nella query
	 * 
	 * Esempi di utilizzo:
	 * 
	 * ArrayList<DbFields> Condictions= new ArrayList<DbFields> ();
	 * Condictions.add(new DbFields("intero","12",DbFields.Type.Int));
	 * Condictions.add(new DbFields("dec1","descrizione1.1",DbFields.Type.String));
	 * WorkingOnDB.getRecordSet("tbltest", Condictions);
	 * 
	 * oppure nel caso in cui non ci sono parametri
	 * WorkingOnDB.getRecordSet("tbltest",new ArrayList<DbFields>());
	 * 
	 * @param table_name nome della tabella
	 * @param condictions l'array delle condizioni per la keyword
	 * 
	 * @return un oggetto ResultSet il quale pu� essere vuoto o meno
	 * 
	 * @see it.tecweb.database
	 */
	public static ResultSet getRecordSet(String table_name, ArrayList<DbFields> condictions)
	{
		/*
		 * gestione dei log
		 */
		Logger log = Logger.getLogger("WorkingOnDB");
		
		/*
		 * oggetto connection
		 */
		Connection con = null;
		/*
		 * oggetto PreparedStatement
		 */
		PreparedStatement prepStat = null;
		/*
		 * punta ad un campo (oggetto della tabella DbFields)
		 */
		DbFields field;
		/*
		 * indice usato per i for
		 */
		int i;
		
		/*
		 * variabile di ritorno
		 */
		ResultSet objects = null;
		

		/*
		 * check dei valori passati in ingresso
		 */
		if (table_name.length()<=0)
		{
			System.out.println("Errore nella funzione WorkingOnDB.DeleteReal campo table_name nullo! ");
		}
		else
		{
	        try
	        {
	        	/*
	        	 * recupero una connessione dall'pool
	        	 */
	        	con = ConnectionPool.getConnectionPool().getConnection();

	        	
		        /*
		         *  costruisco la query per l'update in base ai 
		         *  parametri passati dall'utente
		         */
		        String sql = "SELECT * FROM " + table_name + " ";
		        
	        	/*
	        	 * inserisco le condizioni per il filtro
	        	 */
	        	for (i=0;i<condictions.size();i++)
	        	{
	        		if (i==0)
	        		{
	        			sql += " WHERE  " + condictions.get(i).getField().toString() + " = ?";
	        		}
	        		else
	        		{
	        			sql += " AND " + condictions.get(i).getField().toString() + " = ? ";
	        		}
	        	}

	        	/*
	        	 * preparo l'oggetto PreparedStatement
	        	 */
		        prepStat = (PreparedStatement) con.prepareStatement(sql);
		        
        		/*
        		 * aggiungo le condizioni
        		 */
	        	for (i=0;i<condictions.size();i++)
	        	{
	        		field=(DbFields)condictions.get(i);
	        		if (field.getType()==DbFields.Type.String)
	        		{
	        			prepStat.setString(i+1, field.getValue());	
	        		}
	        		else if (field.getType()==DbFields.Type.Date || field.getType()==DbFields.Type.DateTime)
	        		{
	        			prepStat.setString(i+1, field.getValue());
	        		}
	        		else if (field.getType()==DbFields.Type.Bool)
	        		{
	        			prepStat.setBoolean(i+1, Boolean.getBoolean(field.getValue()));
	        		}
	        		else if (field.getType()==DbFields.Type.Int)
	        		{
	        			int value = Integer.parseInt(field.getValue().trim());
	        			prepStat.setInt(i+1, value);
	        		}
	        	}

	        	/*
	        	 * Lancio l'esecuzione della query e prendo il risultato
	        	 */
	        	objects = (ResultSet) prepStat.executeQuery();

		        /*
		         * rilascio la connessione nel pool
		         */
		        ConnectionPool.getConnectionPool().releaseConnection(con);  
	        }
	        catch(Exception e) 
	        {
	        	log.log(Level.WARNING,e.getMessage());
	        }
	        
		}
		
		return objects;
	}

	
	/**
	 * Questa funzione restituisce un oggetto ResultSet con i valori dei campi
	 * letti nella query
	 * 
	 * Esempi di utilizzo:
	 * 
	 * WorkingOnDB.getRecordSet("Select * from TabTest where id=123");
	 * 
	 * @param sql la query sql
	 * 
	 * @return un oggetto ResultSet il quale pu� essere vuoto o meno
	 * 
	 * @see it.tecweb.database
	 */
	public static ResultSet getRecordSet(String sql)
	{
		/*
		 * gestione dei log
		 */
		Logger log = Logger.getLogger("WorkingOnDB");
		
		/*
		 * oggetto connection
		 */
		Connection con = null;
		/*
		 * oggetto PreparedStatement
		 */
		PreparedStatement prepStat = null;
		/*
		 * variabile di ritorno
		 */
		ResultSet objects = null;
		
        try
        {
        	/*
        	 * recupero una connessione dall'pool
        	 */
        	con = ConnectionPool.getConnectionPool().getConnection();

	        	
        	/*
        	 * preparo l'oggetto PreparedStatement
        	 */
	        prepStat = (PreparedStatement) con.prepareStatement(sql);
		        

        	/*
        	 * Lancio l'esecuzione della query e prendo il risultato
        	 */
        	objects = (ResultSet) prepStat.executeQuery();

	        /*
	         * rilascio la connessione nel pool
	         */
	        ConnectionPool.getConnectionPool().releaseConnection(con);  
        }
        catch(Exception e)
        {
        	log.log(Level.WARNING,e.getMessage());
        }
		return objects;
	}
	
	
	/**
	 * Questa funzione restituisce vero o falso a secondo se un oggetto
	 * ResultSet � pieno o vuoto
	 * 
	 * Esempi di utilizzo:
	 * 
	 * WorkingOnDB.IsEmptyRecordSet(myRecordSet);
	 * 
	 * @param rs ResultSet da controllare
	 * 
	 * @return restituisce un valore true o false
	 * 
	 * @throws SQLException the SQL exception
	 * 
	 * @see it.tecweb.database
	 */
	public static boolean IsEmptyRecordSet(ResultSet rs) throws SQLException
	{
		/* Accedo al primo record del ResultSet*/
		boolean trovati=rs.first();
		
		if (trovati==true)
		{
			return false;
		}
		else
		{
			return true;
		}
	}

}