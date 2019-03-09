REM Copyright (c) 2018 Fast River Technologies Inc. Irvine, CA, USA 
REM All Rights Reserved.
REM Product Build Environment
REM %1 - <patient-json-source-dir> (required), Directory where FHIR Patient resource json files locate, e.g. .\data
REM %2 - <load-patient-json-limit> (optional), Max number of patient json files to be loaded, optional, will load all patients from <sourceDir>  
REM @echo off
%JAVA_HOME%\bin\java -cp "../lib/load/db/*;../lib/load/*;../lib/*" com.frt.fhir.load.FhirLoad %1 %2