Bank of Canada Java Quiz (Climate)
====================================

- Spring Boot with Maven and Spring IO Platform for dependency management
- Thymeleaf with Java 8
- Maven Wrapper included

Prerequisites
-------------

- JDK 8 and JAVA_HOME environment variable set 

Building the project
--------------------

Clone the repository:

> git clone git@github.com:kennychangca/boc.climate.git

Navigate to the newly created folder:

> cd boc.climate

To package and run unit tests:

> mvn clean install

Run the project directly with:

> mvn -q spring-boot:run


Project UI Pages
--------------------
	- {localhost}:8080/               => Display full list of climate data
	- {localhost}:8080/detail/{id}    => Display a specified climate data by id


Design Decisions
--------------------
	{UI}
	
		- Empty data are displayed with na to allow more clear clickable and consistency

	{Data}
	
		- String fields such as Station Name/Province is a mendatory field that shouldn't be empty
		- Date field needs to be a non empty and valid date entry
		- Temperature fields are mendatory and should have decimal field values or empty
		- Data source is only supporting .csv as input on application hosted server
		
	{Service}
	
		- Invalid date or empty station name will be skipped and not loaded into dataset and hence will not be shown on summary page (UI)
		- Temperture fields are critical, non valid data will cause system to throw exception and service will not start
		- In "application.properties" file, a error theshold (percentage of invalid dataset) is introduced to determine wheather service should fail out on exception
		
UI Limitations
--------------------
	- Build with more modern JS liberary, hence some UI features will not be compatible with older version of IEs
	- Summary data are all listed in one page, not in papable fashion
	- Data are kept on client side once loaded and content liveness  is not sync periodically with server data
	

