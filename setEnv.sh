#!/bin/bash
## Fast River Technologies Development Environment  
## Set up common env vars for dev environment on linux
## to be invoked by other script to get env vars set to the 
## proper values
## Note, the values here are set to a typical dev setup on
## a linux box, customize it accordingly
export JAVA_HOME=$HOME/jdk-11.0.2
export JRE_HOME=$HOME/jdk-11.0.2
export ANT_HOME=$HOME/apache-ant-1.9.13
export MAVEN_HOME=$HOME/apache-maven-3.6.0
export PATH=$JAVA_HOME/bin:$MAVEN_HOME/bin:$ANT_HOME/bin:$PATH
export ANT_OPTS=-Xmx512m
export MAVEN_OPTS=-Xmx512m

## Apache Tomcat Home
export CATALINA_HOME=$HOME/apache/apache-tomcat-9.0.16
export CATALINA_BASE=$HOME/apache/apache-tomcat-9.0.16

## Apache Derby Home - on linux use SP
## set DERBY_HOME=C:\db-derby-10.14.2.0
## set PATH=%DERBY_HOME%\bin;%PATH%

## Apache Kafka Home
export KAFKA_HOME=$HOME/kafka_2.12-2.1.1
export PATH=$KAFKA_HOME/bin:$PATH
