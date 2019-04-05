#!/bin/bash
## Copyright(c) 2018 Fast River Technologies Inc. All Rights Reserved. 
## @echo off

source ../env.sh

$JAVA_HOME/bin/java -classpath "../lib/jersey1.9/*:../lib/*" com.frt.dl.service.DataLakeService $1 $2
