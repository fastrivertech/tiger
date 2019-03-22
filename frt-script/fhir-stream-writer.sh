#!/bin/bash
## Copyright(c) 2018 Fast River Technologies Inc. All Rights Reserved. 
## @echo off

source ../setEnv.sh

$JAVA_HOME/bin/java -classpath "../lib/*" com.frt.stream.io.FhirStreamWriter $1
