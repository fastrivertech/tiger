REM Copyright (c) 2018 Fast River Technologies Inc. Irvine, CA, USA 
REM All Rights Reserved.
REM Product Build Environment
REM @echo off
java -cp "./frt-fhir/fhir-rest/target/frt-fhir-rest/WEB-INF/classes;./frt-fhir/fhir-rest/target/frt-fhir-rest/WEB-INF/lib/*" com.frt.fhir.store.Loader %1 %2