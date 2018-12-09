REM Copyright (c) 2018 Fast River Technologies Inc. Irvine, CA, USA 
REM All Rights Reserved.
REM Product Build Environment
@echo off
set JAVA_HOME=C:\JDK10.0.1
set JRE_HOME=C:\JRE10.0.1
set ANT_HOME=D:\apache-ant-1.10.4
set MAVEN_HOME=D:\apache-maven-3.5.4
set PATH=%JAVA_HOME%/bin;%MAVEN_HOME%/bin;%ANT_HOME%/bin;%PATH%
set ANT_OPTS=-Xmx512m
set MAVEN_OPTS=-Xmx512m
set FRT_SPRINGBOOT_TARGET=.\frt-fhir\fhir-springboot\target
set CATALINA_HOME=D:\APACHE\apache-tomcat-9.0.8
set CATALINE_BASE=D:\APACHE\apache-tomcat-9.0.8\base

if exist %FRT_SPRINGBOOT_TARGET% if exist %FRT_SPRINGBOOT_TARGET%\classes if exist %FRT_SPRINGBOOT_TARGET%\lib if exist %FRT_SPRINGBOOT_TARGET%\frt-fhir-springboot.jar goto GOOD2GO
echo "Required classes, jar(s) not available, Please run mvn clean install -Dmaven.test.skip=true to build frt spring boot first, then run_springboot.bat"
GOTO END

:GOOD2GO
java -cp %FRT_SPRINGBOOT_TARGET%\frt-fhir-springboot.jar;%FRT_SPRINGBOOT_TARGET%\lib\*;%FRT_SPRINGBOOT_TARGET%\classes com.frt.fhir.springboot.rest.FHIRApplication

:END