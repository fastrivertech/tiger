#!/bin/bash
## Copyright(c) 2018 Fast River Technologies Inc. All Rights Reserved. 
## @echo off

source ../env.sh
## $1 data base dir $2 interval $3 $4 $5 $6
echo "param cnt:" $#
suffix="_FHIR_MESSAGES"
export SYNTHEA_HOME=../synthea

if (( $# < 3 )); then
  echo "usage: fhir-stream-feeder.sh <base.dir> <interval> [<state-1> <population-1> ...<state-k> <population-k>]"
  echo "example: fhir-stream-feeder.sh ./data 5000 California 1000 Arizona 600 Washington 980"
  exit 0
else
  datadir="$1"; shift
  interval="$1"; shift  
  echo "datadir:" $datadir "interval:" $interval
fi

while [ -n "$1" ]
do
    state="$1"; shift
    if [ -n "$1" ]; then
        pop="$1"; shift
    else
	echo "malformed arguments, each <state> requires <population>..."   
        echo "usage: fhir-stream-feeder.sh <base.dir> <interval> [<state-1> <population-1> ...<state-k> <population-k>]"
        echo "example: fhir-stream-feeder.sh ./data 5000 California 1000 Arizona 600 Washington 980"
        exit 0
    fi
    echo "state:" $state "pop:" $pop
    echo "path:" "$datadir/$state"
    mkdir -p $datadir/$state
    srcdir="$datadir/$state/fhir"
    destdir="$datadir$suffix/$state"
    mkdir -p $destDir
    $SYNTHEA_HOME/bin/run_synthea.sh -p $pop --exporter.baseDirectory "$datadir/$state" $state 
    $JAVA_HOME/bin/java -classpath "../lib/loader/db:../lib/load/*:../lib/*" com.frt.fhir.load.FhirBundleExtract $srcdir $destdir
    unset srcdir;
    unset destdir;
    unset state;
    unset pop;
done

feeder_base="$datadir$suffix"
$JAVA_HOME/bin/java -classpath "../lib/*" com.frt.stream.io.FhirStreamWriter $feeder_base $interval
