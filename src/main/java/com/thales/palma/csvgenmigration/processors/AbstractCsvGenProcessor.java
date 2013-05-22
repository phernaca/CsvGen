package com.thales.palma.csvgenmigration.processors;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Properties;

import com.thales.palma.csvgenmigration.exceptions.CsvGenMigrationException;


public abstract class AbstractCsvGenProcessor implements CsvGenProcessor {

	protected Collection<File> csvInputFiles;
	
	public Context getContext() {
		// TODO Auto-generated method stub
		return null;
	}

	public void initialize(Properties props, Context context)
			throws CsvGenMigrationException {
		
		
		this.csvInputFiles = (Collection<File>)context.get(Context.CSV_INPUT_FILES_KEY);
		
		
		

	}

	/**
	 * 
	 */
	public void processFiles() {
		
		for ( Iterator<File> csvInIter = getCsvInputFiles().iterator(); csvInIter.hasNext(); ) {
			
			try {
				
				
				processOneFile(csvInIter.next());
				
				
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	
	
	public abstract void processOneFile(File csvFile) throws FileNotFoundException;
	
	
	protected Collection<File> getCsvInputFiles() {
		return csvInputFiles;
	}

	
	
}
