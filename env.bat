@echo off
REM Fast River Technologies Development Environment  
set JAVA_HOME=C:\Java\jdk-10.0.2
set JRE_HOME=C:\Java\jre-10.0.2
set ANT_HOME=C:\apache\ant\apache-ant-1.10.5
set MAVEN_HOME=C:\apache\maven\apache-maven-3.5.4
set PATH=%JAVA_HOME%/bin;%MAVEN_HOME%/bin;%ANT_HOME%/bin;%PATH%
set ANT_OPTS=-Xmx512m
set MAVEN_OPTS=-Xmx512m

REM Apache Tomcat Home
set CATALINA_HOME=C:\apache\tomcat\apache-tomcat-9.0.12
set CATALINA_BASE=C:\apache\tomcat\apache-tomcat-9.0.12

REM Apache Derby Home
set DERBY_HOME=C:\apache\derby\db-derby-10.14.2.0
set PATH=%DERBY_HOME%\bin;%PATH%

REM Apache Kafka Home
set KAFKA_HOME=C:\apache\kafka\kafka_2.12-2.1.0
set PATH=%KAFKA_HOME%\bin\windows;%PATH%
