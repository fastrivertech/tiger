REM Copyright (c) 2018 Fast River Technologies Inc. Irvine, CA, USA 
REM All Rights Reserved.
REM Product Build Environment
@echo off
set JAVA_HOME=C:\Java\jdk-10.0.2
set JRE_HOME=C:\Java\jre-10.0.2
set ANT_HOME=C:\apache\ant\apache-ant-1.10.5
set MAVEN_HOME=C:\apache\maven\apache-maven-3.5.4
set MAVEN_HOME=D:\apache-maven-3.5.4
set PATH=%JAVA_HOME%/bin;%MAVEN_HOME%/bin;%ANT_HOME%/bin;%PATH%
set ANT_OPTS=-Xmx512m
set MAVEN_OPTS=-Xmx512m

set CATALINA_HOME=C:\fastrivertech-dev\apache-tomcat-9.0.12
set CATALINE_BASE=C:\fastrivertech-dev\apache-tomcat-9.0.12\base

