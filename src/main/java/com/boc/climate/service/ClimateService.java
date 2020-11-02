package com.boc.climate.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.boc.climate.api.ClimateDataModel;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;

import lombok.var;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Service
public class ClimateService {
	
	private static final Logger logger = LogManager.getLogger(ClimateService.class);
	private List<ClimateDataModel> climateDataList; 
	
	
	@Value("${csv.location}")
	private String inputCsvFilePath;
	
	@Value("${error.threshold}")
	private int errorThreshold;
	
	private int numErrorRecord;
	
	@PostConstruct
	public void init() {
				
		validateStringProvided(inputCsvFilePath, "CSV input file path",0);
		
		if (!isValidFilePath(inputCsvFilePath)) {			
			throw new IllegalArgumentException("Invalid CSV file provided");			
		}
				
		// Parse CSV file to create a list of climatDataModel objects
		try {
			
			Reader reader = new BufferedReader(new FileReader(inputCsvFilePath));
        	
	        CsvToBean<ClimateDataModel> csvReader = new CsvToBeanBuilder(reader)
	             .withType(ClimateDataModel.class)
	             .withSeparator(',')
	             .withSkipLines(1)
	             .withIgnoreLeadingWhiteSpace(true)
	             .withIgnoreEmptyLine(true)
	             .build();
	        	        		
	        List<ClimateDataModel> results = csvReader.parse();
	        if (results == null) {
	        	throw new IllegalArgumentException("Invalid CSV file provided");
	        }
	        	
	        climateDataList = results;
	        // close the reader
	        reader.close();
		}
		catch(Exception ex) {
			// Log Error
			logger.error("Illegal CSV input file. Ex: " + ex.getMessage()); 
			throw new IllegalArgumentException("Invalid CSV file provided");
		}
        
        validateClimateDataAndSetId();
        
	}
	public void validateClimateDataAndSetId() {
		
		List<String> skippedDataMessageList = new ArrayList<String>();		        
    	int id=1;
    	for (var data: climateDataList) {
    		data.setId(id);
    		validateDataset(data, skippedDataMessageList);    	            	    
    		id++;        		
    	}       
    	
    	// Log all skipped error
		for (var skippedDataMessage: skippedDataMessageList ) {
			logger.warn(skippedDataMessage);
		}
		numErrorRecord = skippedDataMessageList.size();
		int errorRate = 100* numErrorRecord / climateDataList.size(); 
		if (errorRate > errorThreshold) {
			throw new IllegalArgumentException("Too many invalid entries on provided CSV file");
		}
		
	}
	public boolean validateDataStringProvided(String param, String paramName, int rowNumber, List<String> skippedDataMessageList) {
		
		if (isEmpty(param)) {
			skippedDataMessageList.add(paramName + "@ Row[" + rowNumber + "] must be provided");
		}
		return true;
	}
	
	public boolean validateDataDateProvided(Date date, int rowNumber, List<String> skippedDataMessageList) {
		if (date == null) {
			skippedDataMessageList.add("Date@ Row[" + rowNumber + "] must be provided");
			return false;
		}
		return true;
	}
	
	public void validateStringProvided(String param, String paramName, int rowNumber) {
		
		if (isEmpty(param)) {
			logger.error("{} must be provided", paramName);
        }			
	}
	
	public static boolean isEmpty(String param) {
		
        return param == null || param.isEmpty();
    }
	
	public static boolean isValidFilePath(String path) {
		
	    File f = new File(path);
	    try {
	       f.getCanonicalPath();
	       return true;
	    }
	    catch (IOException e) {
	       return false;
	    }
	}
		
	public boolean validateDataset(ClimateDataModel data, List<String> skippedDataMessageList) {
		
		int rowId = data.getId();
		boolean result = validateDataStringProvided(data.getStationName(), "Station Name", rowId, skippedDataMessageList)
						 && validateDataStringProvided(data.getProvince(), "Province", rowId, skippedDataMessageList)
						 && validateDataDateProvided(data.getDate(), rowId,skippedDataMessageList);
		
		// Note: Validation for other 3 Big Decimal fields are done when parsed 
		return result;
	}
	
	public List<ClimateDataModel> getSummaryList(){
		return climateDataList;
	}
	public ClimateDataModel getClimateData(int id) {
		if (id > climateDataList.size() || id < 1 ) return null;
		return climateDataList.get(id-1);
	}
	public int getNumRecordError() {
		return numErrorRecord; 
	}
	
}
