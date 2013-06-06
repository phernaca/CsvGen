package com.thales.palma.csvgenmigration.processors;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import au.com.bytecode.opencsv.bean.ColumnPositionMappingStrategy;
import au.com.bytecode.opencsv.bean.CsvToBean;

import com.thales.palma.csvgenmigration.beans.SuperTASBean;
import com.thales.palma.csvgenmigration.beans.TASComponentBean;
import com.thales.palma.csvgenmigration.beans.TASItemBean;
import com.thales.palma.csvgenmigration.exceptions.CsvGenMigrationException;



public abstract class AbstractCsvGenProcessor implements CsvGenProcessor {

	protected static final String ENTRIES_SEP_STR = "~~";

	protected static final String CSV_COMMENT_STR = "#";

	protected static final String _3ePatternStr = "-3E-";

	protected static final String n3ePatternStr = "-N3E-";

	protected static final String prtPatternStr = "-PRT-";

	/* Declare Configuration objects */
    protected Context context;
	Properties properties;	
	
	protected String csvLoadAction;
	
	protected Collection<File> csvInputFiles;
	
	public Context getContext() {
		// TODO Auto-generated method stub
		return context;
	}

	public void initialize(Properties props, Context context)
			throws CsvGenMigrationException {
		
		
		this.csvInputFiles = (Collection<File>)context.get(Context.CSV_INPUT_FILES_KEY);
		
		this.csvLoadAction = (String)context.get(Context.CSV_ACTION_KEY);
		
		this.context = context;
		
		this.properties = props;
		

	}

	/**
	 * 
	 */
	public void processFiles() {
		
		CSVWriter loadActionWriter = null;
		
		try {

			Calendar cal = Calendar.getInstance();
			
			 File outDir = (File)getContext().get(Context.CSV_OUTPUT_DIR_KEY);
			 
			 DateFormat dateFormat = new SimpleDateFormat(getProperties().getProperty("csv_gen_migration.action.csvdateformat"));
			 
			 
			 StringBuilder outputFilePathName = new StringBuilder(outDir.getCanonicalPath());
			 outputFilePathName.append(File.separator);
			 outputFilePathName.append(dateFormat.format(cal.getTime()));
			 outputFilePathName.append("-");
			 outputFilePathName.append(getContext().get(Context.CSV_OUTPUT_FILE_CORE_KEY));
			 outputFilePathName.append(getContext().get(Context.CSV_OUTPUT_FILE_SUFFIX_KEY));
			 outputFilePathName.append(CSV_FILE_EXT);
			 
			 File csvOutputFile = new File(outputFilePathName.toString());
			 
			 
			loadActionWriter = new CSVWriter( new FileWriter(csvOutputFile),
						 						CSV_OUT_SEPARATOR,
						 						CSVWriter.NO_QUOTE_CHARACTER);
			
			String entriesStr = writeHeaderLine();
			 
			String[] entries = entriesStr.split(ENTRIES_SEP_STR);
			 
			loadActionWriter.writeNext(entries); 
			
			
			for ( Iterator<File> csvInIter = getCsvInputFiles().iterator(); csvInIter.hasNext(); ) {
				
				try {
					
					processOneFile(csvInIter.next(), loadActionWriter);
					
					
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			
			if(loadActionWriter != null) {
				try {
					
					loadActionWriter.close();
					
				} catch (IOException e) {
					loadActionWriter = null;
				}
			}
		}	

	}

	
		
	
	public abstract void processOneFile(File csvFile, CSVWriter loadActionWriter) throws FileNotFoundException;

	
	protected  abstract void printCsvFile(List<? extends SuperTASBean> tasBeanList, CSVWriter loadActionWriter);
	
	
	protected Collection<File> getCsvInputFiles() {
		return csvInputFiles;
	}

	public String getCsvLoadAction() {
		return csvLoadAction;
	}

	public Properties getProperties() {
		return properties;
	}

	
	/**
	 * Map the attributes of a TAS Component (3E and N3E)
	 * @param reader
	 * @return
	 */
	protected List<? extends SuperTASBean> obtainComponentTASBeans(CSVReader reader) {
		
		
		ColumnPositionMappingStrategy<TASComponentBean> mappingStrategy = 
	             new ColumnPositionMappingStrategy<TASComponentBean>();
			 	mappingStrategy.setType(TASComponentBean.class);
			 
			 
			 // the fields to bind do in your JavaBean
	          String[] columns = 
	        		  new String[] { "functionName",
			  							"_01legacyIterationIdentifier", "_02legacyMasterIdentifier", "_03legacyOriginIdentifier","_04softType",
			  							"_05number", "_06name", "_07view", "_08partType",
			  							"_09genericType", "_10source", "_11version", 
			  							"_12defaultUnit", "_13defaultTraceCode",
			  							"_15container", "_16folder", "_17id", "_18cage",
			  							"_19icd", "_20enLabel", "_21frLabel",
			  							"_22description", "_23technicalClassification", "_29shelfLife",
			  							"_30productMaturity", "_31COTS", "_33softwareBuildID",
			  							"_34authorizationCode", "_35obsolescenceStatus", "_37LBODate",
			  							"_38localPPLStatus", "_46PPLStatus", "_48startEffectivityDate",
			  							"_59smd", "_60validSourcesCount", "_61lastSourceChange",
			  							"_62PPLApplication", "_63biNumber", "_65selectedSource", 
			  							"_90releasedOn", "_95lifeCycleState"};
	          
	           mappingStrategy.setColumnMapping(columns);	
	            
	            
	            CsvToBean<TASComponentBean> csv = new CsvToBean<TASComponentBean>();
	            List<TASComponentBean> tasBeanList = csv.parse(mappingStrategy, reader);
		
	            
	           return tasBeanList;
	}

	
	/**
	 * Map the attributes of a TAS Items (IHW)
	 * @param reader
	 * @return
	 */
	protected List<? extends SuperTASBean> obtainItemTASBeans(CSVReader reader) {
		
		ColumnPositionMappingStrategy<TASItemBean> mappingStrategy = 
	             new ColumnPositionMappingStrategy<TASItemBean>();
		mappingStrategy.setType(TASItemBean.class);
		
	 	 // the fields to bind do in your JavaBean
	    String[] columns = 
	    		new String[] { "functionName",
	  							"_01legacyIterationIdentifier", "_02legacyMasterIdentifier", "_03legacyOriginIdentifier","_04softType",
	  							"_05number", "_06name", "_07view", "_08partType",
	  							"_09genericType", "_10source", "_11version", 
	  							"_12defaultUnit", "_13defaultTraceCode", "_14endItem",
	  							"_15container", "_16folder", "_17id", "_18cage",
	  							"_19icd", "_20enLabel", "_21frLabel",
	  							"_22description", "_23technicalClassification", "_24unitTracking",
	  							"_25reusable", "_26repairable", "_27isLogisticAssembly",
	  							"_28logisticStatus", "_29shelfLife",
	  							"_30productMaturity", "_31COTS", "_32softwareBaselineID", 
	  							"_33softwareBuildID", "_34authorizationCode", "_35obsolescenceStatus", 
	  							"_36isPhantom", "_37LBODate",
	  							"_38localPPLStatus", "_40functionalStatus",
	  							"_41baseTrackingIdentifier", "_42itemType", "_48startEffectivityDate", 
	  							"_49spareable", "_90releasedOn", "_95lifeCycleState",
	  							"_96creator", "_97modifier", "_98createTimestamp",
	  							"_99modifyTimestamp", "_900qualityDef", "_907model", "_908pdmPart"};
	
	     
		mappingStrategy.setColumnMapping(columns);
		
		CsvToBean<TASItemBean> csv = new CsvToBean<TASItemBean>();
	    List<TASItemBean> tasBeanList = csv.parse(mappingStrategy, reader);
			 	
		return tasBeanList;
	}

	
	/**
	 * Writes the Header of the CSV File
	 * @return
	 */
	protected String writeHeaderLine() {
		
		StringBuilder entriesStr = new StringBuilder(CSV_COMMENT_STR);
		 entriesStr.append( getCsvLoadAction() );
		 entriesStr.append(ENTRIES_SEP_STR);
		 entriesStr.append("05number");
		 entriesStr.append(ENTRIES_SEP_STR);
		 entriesStr.append("07view");
		 entriesStr.append(ENTRIES_SEP_STR);
		 entriesStr.append("11version");
		 
		 
		return entriesStr.toString();
		
	}

	
}
