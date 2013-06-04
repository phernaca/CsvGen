package com.thales.palma.csvgenmigration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import com.thales.palma.csvgenmigration.exceptions.CsvGenMigrationException;
import com.thales.palma.csvgenmigration.processors.Context;
import com.thales.palma.csvgenmigration.processors.ContextBase;
import com.thales.palma.csvgenmigration.processors.CsvGenProcessor;



public class CsvGenMigrationAction {

	protected static final String ERR_IS_NOT_A_VALID_FOLDER_MSG = " is Not a valid Folder!";

	public final static Logger fileMigrLog = java.util.logging.Logger.getLogger(CsvGenMigrationAction.class.getName());
	
	/**
	 * Properties
	 */
	Properties properties;
	
	
	
	/**
	 * Setup CsvGenMigrationAction Logger (use java.util.logging.Logger)
	 * @throws IOException 
	 * @throws SecurityException 
	 */
	public void setLogsCsvGenMigrationLogger(String outputLogDir) throws SecurityException, IOException {
				
		FileHandler fhandler = new FileHandler(outputLogDir + File.separator 
				+ getProperties().getProperty("csv_gen_migration.action.logging.filename", "CsvGenLogs.log"), true);		
				fhandler.setFormatter(new Formatter() {
				public String format(LogRecord record) {
				DateFormat dateFormat = 
					new SimpleDateFormat(
								getProperties().getProperty("csv_gen_migration.action.logging.dateformat", "yyyy-MM-dd HH:mm:ss") );
				Calendar cal = Calendar.getInstance();
				
				return dateFormat.format(cal.getTime()) + "  :  "			          		             
				+ record.getMessage() + "\n";
				}
				});

		fileMigrLog.addHandler(fhandler);
		
		String level = getProperties().getProperty("csv_gen_migration.action.logging.level", "INFO");
		fileMigrLog.setLevel(
				java.util.logging.Level.parse(level) );
		
	}
	
	
	/**
	 * @param loadAction
	 * 
	 */
	protected  void launchProcessor(String loadAction, Context context) {
		/* Obtain the instance of the right Processor */
		try {
			
			fileMigrLog.info("Proceed with action: " + loadAction);
			
			CsvGenProcessor lprocessor = getCsvProcessor(loadAction);
							
			/* Then proceed to launch the work(on matching folder) with this processor ...if it has been well instantiated. */		
			if(lprocessor != null) {
						
				context.set(Context.PROCESSOR_CLASS_KEY, lprocessor.getClass());
				
				fileMigrLog.info("Instantiated Processor: " + lprocessor.getClass());
				/* Configure it to be ready to work */
				lprocessor
						.initialize(getProperties(),
									context);
				
				
				fileMigrLog.info("Process files!");
				/* Start importing the files */
				lprocessor.processFiles();
				
			}
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Create an CsvGenProcessor instance from the class name that must have been previously set into context.
	 * @return an CsvGenProcessor implementation instance
	 * @throws CsvGenMigrationException
	 */
	protected CsvGenProcessor getCsvProcessor(String csvAction) throws CsvGenMigrationException {

		/**
		 * Load Processor dynamically
		 */
		/* Then obtain the right implementation Logs Processor class 
		 * First get the Load Type Name and then the Processor class Name */
		String implProcessorClassName =getProperties().getProperty("csv_gen_migration." + csvAction + ".processorclassname","");

		// FileProcessor reference
		CsvGenProcessor logsProcessor = null;
		
		// Create an instance of the handler implementation 
		try {
				
			if(StringUtils.isNotBlank(implProcessorClassName)) {
				logsProcessor = (CsvGenProcessor)Class.forName(implProcessorClassName).newInstance();
			}	

		} catch (Exception e) {
			throw new CsvGenMigrationException("Failed to load class " + implProcessorClassName, e);
		}
		
		return logsProcessor;
	}



	public Properties getProperties() {
		return properties;
	}
	
	
	public void setProperties(Properties properties) {
		this.properties = properties;
	}


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		if (args.length < 3) {
			
			System.out.println(" Call CsvGenMigrationAction with three arguments : \n" + 
					"0 -> Action  \n1 -> Full Path with the Folder containing all CSV files \n2 -> Full Path Output Folder." +
					"\n3(optional) -> Logs Folder");
			
			System.exit(1);
		}
		
		
		try {
			
			/* Obtain Properties object with contents from 'csv_gen_migration.properties' file */
			Properties csvGenProperties = new Properties();
			InputStream isProps = ClassLoader.getSystemResourceAsStream("csv_gen_migration.properties");
			csvGenProperties.load(isProps);
			
			String validActionsStr = csvGenProperties.getProperty("csv_gen_migration.action.valid_ones", "");
			
			/* Proceed with Input/Output Folders for the treatment */
			String csvGenAction = args[0];
			String csvInputFolder = args[1];
			String csvOutputFolder = args[2];
			
			/* Proceed to check all arguments */
			if(StringUtils.isEmpty(csvGenAction)
					|| !(StringUtils.contains(validActionsStr, csvGenAction))) {
				throw new CsvGenMigrationException(csvGenAction + " is Not a valid Action!");
			}
			
			/* Obtain Folders with input CSV files and get the full list of files */
			File  csvsInputDir  = new File(csvInputFolder); 
			File  csvsOutputDir  = new File(csvOutputFolder); 
			
			if(!csvsInputDir.isDirectory()) {
				throw new CsvGenMigrationException(csvInputFolder + ERR_IS_NOT_A_VALID_FOLDER_MSG);
			}
			
			if(!csvsOutputDir.isDirectory()) {
				throw new CsvGenMigrationException(csvOutputFolder + ERR_IS_NOT_A_VALID_FOLDER_MSG);
			}
			
			/* Obtain the list of all the files in the Folder */
			Collection<File> csvFiles = obtainCsvFilesList(csvsInputDir);
			
				
			/*  Create a new CsvGenMigrationAction object */ 
			CsvGenMigrationAction mAction = new CsvGenMigrationAction();
			
			/* Initialize Action (Singleton) Object */
			mAction.setProperties(csvGenProperties);
			mAction.setLogsCsvGenMigrationLogger(csvOutputFolder);
			
			/**
			 * Obtain CSV Suffix for the current output CSV file
			 */
			String outCsvSuffix = csvGenProperties.getProperty("csv_gen_migration." + csvGenAction + ".output_file_suffix","");
			
			/**
			 * Create a new context for each Processor
			 */
			Context context = new ContextBase();
			
			context.set(Context.CSV_INPUT_FILES_KEY,csvFiles);
			context.set(Context.CSV_ACTION_KEY,csvGenAction);
			context.set(Context.CSV_OUTPUT_FILE_SUFFIX_KEY,outCsvSuffix);
			context.set(Context.CSV_OUTPUT_DIR_KEY, csvsOutputDir);
			
			
			/* Launch the corresponding Processor */
			mAction.launchProcessor(csvGenAction,context);
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CsvGenMigrationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}



	/**
	 * 
	 * @param csvsDir
	 * @return
	 */
	protected static Collection<File> obtainCsvFilesList(File csvsDir) {
		Collection<File> csvFiles = new ArrayList<File>();
		if(csvsDir.exists() && csvsDir.isDirectory()) {
			String[]   exts   = new String[] {"csv"};  
		    csvFiles = FileUtils.listFiles(csvsDir, exts, true);
		    
		}
		
		return csvFiles;
	}
	
	
	
	
	
}
