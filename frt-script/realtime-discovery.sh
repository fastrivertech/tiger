#!/bin/bash
## Copyright(c) 2018 Fast River Technologies Inc. All Rights Reserved. 
## @echo off

source ../setEnv.sh
exec ./showEnv.sh

$JAVA_HOME/bin/java -classpath "../lib/*" com.frt.stream.application.RealTimeDiscovery