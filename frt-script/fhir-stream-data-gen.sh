#!/bin/bash

source ../env.sh

$JAVA_HOME/bin/java -classpath "../lib/*:../lib/load/*" com.frt.stream.io.FhirStreamDataGen $1
