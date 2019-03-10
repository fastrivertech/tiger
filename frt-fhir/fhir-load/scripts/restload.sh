#!/bin/bash
## Copyright (c) 2018 Fast River Technologies Inc. Irvine, CA, USA 
## All Rights Reserved.
## Product Build Environment
## $1 - <fhir-patient-rest-url> (required), URL of FHIR rest service end point, e.g. http://localhost:8080/frt-fhir-rest/1.0/Patient
## $2 - <patient-json-source-dir> (required), Directory where FHIR Patient resource json files locate, e.g. .\data
## $3 - <load-patient-json-limit> (optional), Max number of patient json files to be loaded, optional, will load all patients from <sourceDir>  
## @echo off

$JAVA_HOME/bin/java -cp "../lib/load/rest/*:../lib/load/*:../lib/*" com.frt.fhir.load.FhirRestLoad $1 $2 $3