#/bin/bash!

## Copyright (c) 2018 Fast River Technologies Inc. Irvine, CA, USA 
## All Rights Reserved.
## @echo off

source ../env.sh
$JAVA_HOME/bin/java -cp "../lib/loader/db:../lib/load/db/*:../lib/load/*:../lib/*" com.frt.fhir.load.FhirBundleExtract
