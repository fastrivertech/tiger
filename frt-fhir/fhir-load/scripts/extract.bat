REM Copyright (c) 2018 Fast River Technologies Inc. Irvine, CA, USA 
REM All Rights Reserved.
REM @echo off
%JAVA_HOME%\bin\java -cp "../lib/loader/db;../lib/load/db/*;../lib/load/*;../lib/*" com.frt.fhir.load.FhirBundleExtract %1 %2