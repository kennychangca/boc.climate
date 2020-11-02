package com.boc.climate.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Reader;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.boc.climate.api.ClimateDataModel;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;

public class CvsParser {
	
	private String sourcePath_; 
	private static final Logger logger = LogManager.getLogger(CvsParser.class);
	
	public CvsParser(String sourcePath) {
		sourcePath_ = sourcePath;
	}

	public List<ClimateDataModel> parseData() {
		
		// Parse CSV file to create a list of climatDataModel objects
		try {
					
			Reader reader = new BufferedReader(new FileReader(sourcePath_));
		        	
		    CsvToBean<ClimateDataModel> csvReader = new CsvToBeanBuilder(reader)
		         .withType(ClimateDataModel.class)
		         .withSeparator(',')
		         .withSkipLines(1)
		         .withIgnoreLeadingWhiteSpace(true)
		         .withIgnoreEmptyLine(true)
		         .build();
			        	        		
		    List<ClimateDataModel> results = csvReader.parse();
		    if (results == null) {
		      	logger.error("Invalid CSV file provided");
		      	return null;
		    }
		    // close the reader
		    reader.close();        	
		    return results;
		    
		}
		catch(Exception ex) {
			// Log Error
			logger.error("Illegal CSV input file. Ex: " + ex.getMessage());
			return null;
			// throw new IllegalArgumentException("Invalid CSV file provided");
		}				
	}
}
