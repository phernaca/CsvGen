package com.thales.palma.csvgenmigration.processors;

import java.util.Properties;

import com.thales.palma.csvgenmigration.exceptions.CsvGenMigrationException;


public interface CsvGenProcessor {
	
	
	/**
	 * CVS Separator
	 */
	public static String CSV_MAP_FILE_SEPARATOR = "~";
	
	/**
	 * Separator on CSV Error Logs
	 */
	public static final String SEP_LINE_ID = "|";
	
	/**
	 * Initializes the implementation class with Objects containing 
	 * all the needed information for processing (logs and input csv)files
	 * @param config
	 * @param context
	 */
	public void initialize(Properties props, Context context) throws CsvGenMigrationException;
		
	/**
	 * Process all the files
	 */
	public void processFiles();
	
	/**
	 * Container with main informations to process
	 * @return
	 */
	public Context getContext();

}
