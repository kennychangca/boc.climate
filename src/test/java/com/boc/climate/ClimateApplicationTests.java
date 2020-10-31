package com.boc.climate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.system.JavaVersion;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.SpringVersion;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.servlet.ModelAndView;

import com.boc.climate.api.ClimateController;
import com.boc.climate.service.ClimateService;

import lombok.var;

@SpringBootTest
@AutoConfigureMockMvc
class ClimateApplicationTests {

	@Autowired
    private MockMvc mockMvc;
	
	@Autowired
	private ClimateService climateService; 
	
	private static final Logger logger = LogManager.getLogger(ClimateController.class);
	
	private static final String CLIMATE_ROOT = "/summary";
	private static final String DETAIL_URL = "/detail";
	
	
	private static final String TOO_MANY_INVALID_DATA_MSG = "Too many invalid entries on provided CSV file";
	
	@Test
	void JavaFrameworkVersionTest() {
		
		assertEquals("5.2.9.RELEASE", SpringVersion.getVersion());	
		assertEquals("1.8", JavaVersion.getJavaVersion().toString());
	}
	
	@Test
	public void ClimateDateSummaryTest() {
		
		try {
			MvcResult result = mockMvc.perform(get(CLIMATE_ROOT)
					.contentType(MediaType.APPLICATION_JSON_UTF8))
					.andDo(print())			
					.andExpect(status().isOk())
					.andReturn();
			
			var resultList = result.getModelAndView().getModelMap().get("summaryList");			 
			
			
		} catch (Exception ex) {
			
			logger.error(ex.getMessage());
		}		
	}
	
	@Test
	public void ClimateDateDetailTest() {
		var dataList = climateService.getSummaryList();	
		var lastRow = dataList.size();
		validateClimateDateDetail(1);
		validateClimateDateDetail(lastRow);
	}
	
	public void validateClimateDateDetail(int rowNum) {
			
		try {
			var dataList = climateService.getSummaryList();	
			MvcResult result = mockMvc.perform(get(DETAIL_URL + "/" + rowNum)
				.contentType(MediaType.APPLICATION_JSON_UTF8))
				.andDo(print())			
				.andExpect(status().isOk())
				.andReturn();
			
			var resultModel = result.getModelAndView().getModel();
			var dataModel = dataList.get(rowNum-1);
			
			assertEquals(dataModel.getStationName(), resultModel.get("stationName"));
			assertEquals(dataModel.getProvince(), resultModel.get("province"));
			assertEquals(dataModel.getDate(), resultModel.get("date"));
			assertEquals(dataModel.getMeanTemp(), resultModel.get("meanTemp"));
			assertEquals(dataModel.getHighestMonthlyMaxTemp(), resultModel.get("highestMonthlyMaxTemp"));
			assertEquals(dataModel.getLowestMonthlyMinTemp(), resultModel.get("lowestMonthlyMinTemp"));
						
			} catch (Exception ex) {
				logger.error(ex.getMessage());
			}			
	}
		
	
	@Test	
	public void dataMissingTest() {
		
		// Case 1: Try to overlap 3 missing fields (Station Name and Province)
		var dataList = climateService.getSummaryList();
		int fullCount = dataList.size(); 
				
		int count=1;
		for (var data: dataList) {
			if (count %2 == 0 && count <= 30) {
				data.setStationName("");
			}
			else if (count % 3 ==0 && count <= 30){				
				data.setProvince("");
			}
			else if (count % 5 ==0 && count <= 30) {
				data.setDate(null);
			}	
			count++;
		}
		climateService.validateClimateDataAndSetId();
		int tmpNum= climateService.getNumRecordError();
		assertEquals(22, climateService.getNumRecordError());	
		
		// Case 2: Try to invalidate more than error threshold and trigger exception
		try {			
			count=0;
			for (var data: dataList) {
				if (count %2 ==0) {
					data.setStationName("");
				}
				count++;
			}
			climateService.validateClimateDataAndSetId();
		}
		catch (Exception ex) {
			assertEquals(TOO_MANY_INVALID_DATA_MSG, ex.getMessage());
		}		
	}
			
}
