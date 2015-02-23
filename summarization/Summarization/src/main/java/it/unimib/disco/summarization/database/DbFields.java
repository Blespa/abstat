/**
 * Questa classe � usata per rappresentare un generico campo
 * di una tabella del database. Ha il prezioso compito di
 * rendere "astratto" l'oggetto campo.
 * 
 *  
 * Questa classe viene usate nelle funzioni della classe WorkinOnDB.
 * 
 * 
 * @version 1.00
 * @author Gruppo Tec. Web
 * @see it.tecweb.database.WorkingOnDB
 * 
 */
package it.unimib.disco.summarization.database;

/**
 * The Class DbFields.
 */
public class DbFields {

	/** The field. */
	private String field;
	
	/** The value. */
	private String value;
	
	/**
	 * The Enum Type.
	 */
	public enum Type { 
 /** The String. */
 String, 
 /** The Int. */
 Int, 
 /** The Date. */
 Date, 
 /** The Bool. */
 Bool, 
 /** The Date time. */
 DateTime };
	
	/** The type. */
	private Type type;
	
	/* costruttore della classe*/
	
	/**
	 * Instantiates a new db fields.
	 * 
	 * @param field
	 *            the field
	 * @param value
	 *            the value
	 * @param type
	 *            the type
	 */
	public DbFields(String field, String value, Type type) 
	{
		this.field=field;
		this.value=value;
		this.type=type;
	}

	/* set e get delle variabili private della classe */
	
	/**
	 * Gets the field.
	 * 
	 * @return the field
	 */
	public String getField() {
		return field;
	}

	/**
	 * Sets the field.
	 * 
	 * @param field
	 *            the new field
	 */
	public void setField(String field) {
		this.field = field;
	}

	/**
	 * Gets the value.
	 * 
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Sets the value.
	 * 
	 * @param value
	 *            the new value
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * Gets the type.
	 * 
	 * @return the type
	 */
	public Type getType() {
		return type;
	}

	/**
	 * Sets the type.
	 * 
	 * @param type
	 *            the new type
	 */
	public void setType(Type type) {
		this.type = type;
	}

	
}
