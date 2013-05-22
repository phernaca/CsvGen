package com.thales.palma.csvgenmigration.processors;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

import com.thales.palma.csvgenmigration.beans.SuperTASBean;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.bean.ColumnPositionMappingStrategy;
import au.com.bytecode.opencsv.bean.CsvToBean;

public class TASPartConfProcessor extends AbstractCsvGenProcessor {

	
	
	
	@Override
	public void processOneFile(File csvFile) throws FileNotFoundException {
		
		System.out.println("Uses TASPartConfProcessor : Process -> " + csvFile.getName());
		
		
		 CSVReader reader = new CSVReader(new FileReader(csvFile), '|', '\"', 1);
		
		 ColumnPositionMappingStrategy<SuperTASBean> mappingStrategy = 
             new ColumnPositionMappingStrategy<SuperTASBean>();
		 	mappingStrategy.setType(SuperTASBean.class);
		 
		 
		 // the fields to bind do in your JavaBean
          String[] columns = new String[] { "functionName",
        		  							"_01legacyIterationIdentifier", "_02legacyMasterIdentifier", "_03legacyOriginIdentifier","_04softType",
        		  							"_05number", "_06name", "_07view", "_08partType",
        		  							"_09genericType", "_10source", "_11version"}; 
           mappingStrategy.setColumnMapping(columns);	
            
            
            CsvToBean<SuperTASBean> csv = new CsvToBean<SuperTASBean>();
            List<SuperTASBean> tasBeanList = csv.parse(mappingStrategy, reader); 
            
            for(SuperTASBean tasBean : tasBeanList) {
            	
            	System.out.println("Number -> " + tasBean.get_05number() + " Revision -> " + tasBean.get_11version() + " View -> " +  tasBean.get_07view());
            }
		 	
	}

	
}
