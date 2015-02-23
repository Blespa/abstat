/**
 * Questa classe verr� istanziata quando si verifica un 
 * errore nel tentativo di connessione ad una base dati
 * 
 * @version 1.00
 * @author Gruppo Tec. Web
 * 
 */
package it.unimib.disco.summarization.database;

/**
 * The Class ConnectionPoolException.
 */
public class ConnectionPoolException extends Exception {
	/*
	 * Questa variabile � usata per evitare che a runtime 
	 * venga calcolato un identificvativo della classe
	 * 
	 */
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

/* 
	 * la classe non ha corpo perch� � stata pensata 
	 * in modo tale che un qualsiasi programmatore 
	 * possa estendere la presente per creare delle 
	 * azioni custom alla presenza di errori con il 
	 * proprio database.
	 * 
	 */
	/**
 * Instantiates a new connection pool exception.
 */
public ConnectionPoolException() {}
}


 