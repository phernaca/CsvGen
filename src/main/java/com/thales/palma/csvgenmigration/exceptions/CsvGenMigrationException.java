package com.thales.palma.csvgenmigration.exceptions;

public class CsvGenMigrationException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CsvGenMigrationException(){
		super();
	}
	
	public CsvGenMigrationException(String msg){
		super(msg);
	}
	
	/**
     * @param cause
     */
    public CsvGenMigrationException(Throwable cause) {
        super(cause);
    }

    /**
     * @param message
     * @param cause
     */
    public CsvGenMigrationException(String message, Throwable cause) {
        super(message, cause);
    }
	
	

}
