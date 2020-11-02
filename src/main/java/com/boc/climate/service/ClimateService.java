package com.boc.climate.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.boc.climate.api.ClimateDataModel;
import lombok.var;
import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Service
public class ClimateService {
	
	private static final Logger logger = LogManager.getLogger(ClimateService.class);
	private List<ClimateDataModel> climateDataList;
	private List<ClimateDataModel> cleanClimateDataList;
	
	
	@Value("${csv.location}")
	private String inputCsvFilePath;
	
	@Value("${error.threshold}")
	private int errorThreshold;
	
	private int numErrorRecord;
	
	@PostConstruct
	private void init() {
		
		cleanClimateDataList = new ArrayList<ClimateDataModel>();
		validateStringProvided(inputCsvFilePath, "CSV input file path",0);
		
		if (!isValidFilePath(inputCsvFilePath)) {			
			throw new IllegalArgumentException("Invalid CSV file provided");			
		}
				
		// Parse CSV file to create a list of climatDataModel objects
		CvsParser cvsParser = new CvsParser(inputCsvFilePath); 
		climateDataList = cvsParser.parseData();
		if (climateDataList == null) return; 
        validateClimateDataAndSetId();        
        
	}
	public void validateClimateDataAndSetId() {
				
		List<String> skippedDataMessageList = new ArrayList<String>();		        
    	int id=1;
    	int rowNum=1; 
    	for (var data: climateDataList) {    		
    		if (validateDataset(data, rowNum, skippedDataMessageList)) {
    			data.setId(id);
    			cleanClimateDataList.add(data);
    			id++;
    		}    		        		
    		rowNum++;
    	}       
    	
    	// Log all skipped error
		for (var skippedDataMessage: skippedDataMessageList ) {
			logger.warn(skippedDataMessage);
		}
		// Check to see if error count has exceed error threshold rate
		numErrorRecord = skippedDataMessageList.size();
		int errorRate = 100* numErrorRecord / climateDataList.size(); 
		if (errorRate > errorThreshold) {
			throw new IllegalArgumentException("Too many invalid entries on provided CSV file");
		}
		
	}
	private boolean validateDataStringProvided(String param, String paramName, int rowNumber, List<String> skippedDataMessageList) {
		
		if (isEmpty(param)) {
			skippedDataMessageList.add(paramName + "@ Row[" + rowNumber + "] must be provided");
		}
		return true;
	}
	
	private boolean validateDataDateProvided(Date date, int rowNumber, List<String> skippedDataMessageList) {
		if (date == null) {
			skippedDataMessageList.add("Date@ Row[" + rowNumber + "] must be provided");
			return false;
		}
		return true;
	}
	
	private boolean validateDataTempProvided(String param, String paramName, int rowNumber, List<String> skippedDataMessageList) {
		
		if (isEmpty(param)) {			
			return true;
		}
		try {
			float temp = Float.parseFloat(param);
		}
		catch(Exception ex) {	
			skippedDataMessageList.add(paramName + "@ Row[" + rowNumber + "] is a not valid decimal value");
			return false;		
		}
		return true;
	}
	
	private void validateStringProvided(String param, String paramName, int rowNumber) {
		
		if (isEmpty(param)) {
			logger.error("{} must be provided", paramName);
        }			
	}
	
	private static boolean isEmpty(String param) {
		
        return param == null || param.isEmpty();
    }
	
	private static boolean isValidFilePath(String path) {
		
	    File f = new File(path);
	    try {
	       f.getCanonicalPath();
	       return true;
	    }
	    catch (IOException e) {
	       return false;
	    }
	}
		
	private boolean validateDataset(ClimateDataModel data, int rowNum, List<String> skippedDataMessageList) {
		
		boolean result = validateDataStringProvided(data.getStationName(), "Station Name", rowNum, skippedDataMessageList)
						 && validateDataStringProvided(data.getProvince(), "Province", rowNum, skippedDataMessageList)
						 && validateDataDateProvided(data.getDate(), rowNum,skippedDataMessageList)
						 && validateDataTempProvided(data.getMeanTemp(), "Mean Temp", rowNum, skippedDataMessageList)
						 && validateDataTempProvided(data.getHighestMonthlyMaxTemp(), "Highest Monthly Max Temp", rowNum, skippedDataMessageList)
						 && validateDataTempProvided(data.getLowestMonthlyMinTemp(), "Lowest Monthly Min Temp", rowNum, skippedDataMessageList);

		return result;
	}
	
	public List<ClimateDataModel> getSummaryList(){
		return cleanClimateDataList;
	}
	
	public ClimateDataModel getClimateData(int id) {
		if (id > cleanClimateDataList.size() || id < 1 ) return null;
		return cleanClimateDataList.get(id-1);
	}
	
	public int getNumRecordError() {
		return numErrorRecord; 
	}
	
}
