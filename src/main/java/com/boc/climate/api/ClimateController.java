package com.boc.climate.api;

import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.boc.climate.service.ClimateService;
import lombok.var;


@Controller
@RequestMapping("/")
public class ClimateController {
	
	private static final Logger logger = LogManager.getLogger(ClimateController.class);
	private static final String ERROR_INVALID_ENTITY = "invalidEntity";
	private static final String ERROR_INVALID_URL = "invalidUrl";
	
	@Autowired
	private ClimateService climateService; 
	
	@Value("${msg.title}")
	private String title;
	
	@Value("${msg.detail}")
	private String detailPageTitle;
	
	@Value("{errorMsg.invalidUrl}")
	private String invalidUrlErrorMsg;
	
	@Value("{errorMsg.invalidEntity}")
	private String entityNotExsitErrorMsg;
	
	@Value("#{${errorMsg}}")
	Map<String,String> errorMap;	
	
	
	@GetMapping(value = "/summary")
    public String getContacts(Model model) {
		model.addAttribute("summaryList", climateService.getSummaryList());
		model.addAttribute("title", "Climate Summary");
		return "summary";		
	}
	
	@GetMapping(value = "/detail/{meanTempId}")
    public String getDetail(Model model, @PathVariable int meanTempId) {
		
		// climateService
		var data = climateService.getClimateData(meanTempId);
		if (data == null) {
			
			var errorMsg = errorMap.get(ERROR_INVALID_ENTITY);
			model.addAttribute("error", errorMsg);
			// log error
			logger.error("Illegal access to entry. Id={}", meanTempId);
			return "error";
		}
		
		model.addAttribute("detail", detailPageTitle);
		model.addAttribute("stationName", data.getStationName());
		model.addAttribute("province", data.getProvince());
		model.addAttribute("date", data.getDate());
		model.addAttribute("meanTemp", data.getMeanTemp());
		model.addAttribute("highestMonthlyMaxTemp", data.getHighestMonthlyMaxTemp());
		model.addAttribute("lowestMonthlyMinTemp", data.getLowestMonthlyMinTemp());		
		
		return "detail";		
	}	
	// Non-Implemented GET end points handler
	@RequestMapping(value={"/","/{non_existing_url}"}, method = RequestMethod.GET)	
	String invalidPageAccess(Model model, @PathVariable String non_existing_url) {
		var errorMsg = errorMap.get(ERROR_INVALID_URL);
		model.addAttribute("error", errorMsg);
		return "error";
	}		
	
}
