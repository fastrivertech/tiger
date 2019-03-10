#!/bin/bash
## Copyright (c) 2018 Fast River Technologies Inc. Irvine, CA, USA 
## All Rights Reserved.
## Product Build Environment
## $1 - <patient-json-source-dir> (required), Directory where FHIR Patient resource json files locate, e.g. .\data
## $2 - <load-patient-json-limit> (optional), Max number of patient json files to be loaded, optional, will load all patients from <sourceDir>  
## @echo off
$JAVA_HOME/bin/java -cp "../lib/loader/db:../lib/load/db/*:../lib/load/*:../lib/*" com.frt.fhir.load.FhirLoad $1 $2