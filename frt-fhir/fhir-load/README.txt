FRT FHIR Load
-------------
1. Build Tiger Project

2. Unzip \frt-package\target\frt-service-package-1.0.0-SNAPSHOT to any working folder
   load folder contains load tool
   load\data folder contains sample data
   
3. Execute env.bat to setup environment

4. Run load - there are 2 loaders, one that loads patients directly into persistence storage (e.g. Splice Machine - db loader),
   another that loads patients to persistence store through FHIR Patient RESTful service API (web loader)

   db loader usage:

   .\load\load.bat [data folder] [maximum number of data files to load]
   E.g.,    
   .\load\load.bat .\data 5
   E.g.,
   .\load\load.bat .\synthea\bin\output 500

   web loader usage:

   .\load\restload.bat [fhir patient rest service URL] [data folder] [maximum number of data files to load]
   E.g.,    
   .\load\restload.bat "http://localhost:8080/frt-fhir-rest/1.0/Patient" ".\data"
   E.g.,
   .\load\restload.bat "http://localhost:8080/frt-fhir-rest/1.0/Patient" ".\synthea\bin\output" 500
   
FRT FHIR Bundle Extract
-----------------------
1) Configure frt.fhir.load.source.dir and frt.fhir.load.target.dir in .\load\config\frt_load.properties   
2) Execute extract.bat 