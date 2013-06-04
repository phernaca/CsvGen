package com.thales.palma.csvgenmigration.processors;

import java.io.File;

public interface Context {

	
	public static final String PROCESSOR_CLASS_KEY = "csv_processor_class";
	public static final String CSV_INPUT_FILES_KEY = "input_csv_files";
	public static final String CSV_OUTPUT_DIR_KEY = "output_csv_folder";
	public static final String CSV_OUTPUT_FILE_SUFFIX_KEY = "output_csv_file_suffix";
	
	public static final String CSV_ACTION_KEY = "csv_action_key";
	
	
	

	
	/**
	 * Get a value that has been set into the context with the given key
	 * @param key a key
	 * @return a value
	 */
	public Object get(String key);
	
	/**
	 * Set a value into the context with the given key
	 * @param key a key
	 * @param value a value
	 */
	public void set(String key, Object value);
}
