package com.boc.climate.api;

import java.math.BigDecimal;
import java.util.Date;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;
import com.opencsv.bean.CsvDate;

import lombok.Data;

@Data
public class ClimateDataModel {
		 	 	 
	 private int id; 
	 
	 @CsvBindByPosition(position = 0)	 	
	 private String stationName;
	 
	 @CsvBindByPosition(position = 1)
	 private String province;
	 	 
	 @CsvBindByPosition(position = 2)
	 @CsvDate("dd/MM/yyyy")
	 private Date date;
	 
	 @CsvBindByPosition(position = 3)
	 private BigDecimal meanTemp;
	 
	 @CsvBindByPosition(position = 4)
	 private BigDecimal highestMonthlyMaxTemp;
	 
	 @CsvBindByPosition(position = 5)
	 private BigDecimal lowestMonthlyMinTemp;
	 
	 public String getStationName() {
		 return stationName; 
	 }
	 
	 @Override
	 public String toString() {
	     return "ClimateDataModel{" +	            
	            "Station_Name='" + this.stationName + '\'' +
	            ", Province='" + this.province + '\'' +
	            ", Date='" + this.date.toString() + '\'' +
	            ", Mean_Temp='" + meanTemp + '\'' +
	            ", Highest_Monthly_Max_Temp='" + highestMonthlyMaxTemp + '\'' +	            
	            ", Lowest_Monthly_Min_Temp=" + lowestMonthlyMinTemp +
	            '}';
	 }
}
