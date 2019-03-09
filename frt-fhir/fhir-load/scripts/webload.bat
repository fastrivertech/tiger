REM Copyright (c) 2018 Fast River Technologies Inc. Irvine, CA, USA 
REM All Rights Reserved.
REM Product Build Environment
REM %1 - <fhir-patient-rest-url> (required), URL of FHIR rest service end point, e.g. http://localhost:8080/frt-fhir-rest/1.0/Patient
REM %2 - <patient-json-source-dir> (required), Directory where FHIR Patient resource json files locate, e.g. .\data
REM %3 - <load-patient-json-limit> (optional), Max number of patient json files to be loaded, optional, will load all patients from <sourceDir>  
REM @echo off

%JAVA_HOME%\bin\java -cp "../lib/loader/web/*;../lib/loader/*;../lib/*" com.frt.fhir.load.FhirRestLoad %1 %2 %3