REM Copyright(c) 2018 Fast River Technologies Inc. All Rights Reserved. 
@echo off

if ""%2""=="""" (
   echo "Error: fhir-stream-feeder.bat requires at least 2 arguments <base.dir> <interval> ..."
   goto usage
)

set datadir=%1%
shift
set interval=%1
shift

set suffix="_FHIR_MESSAGES"
set "SYNTHEA_HOME=../synthea"

echo base.dir: %datadir% interval: %interval%
echo SYNTHEA HOME %SYNTHEA_HOME% SUFFIX %suffix%

if not exist %datadir% mkdir %datadir%

:loop
    ::-------------------------- check list of state and population size
    if ""%1""=="""" (
		echo Done with synthetic patient json generation.
        goto end
    )
	set state=%1
	shift
    if ""%1""=="""" (
		echo "Error: fhir-stream-feeder.bat requires population size if a state is given."
		goto usage
    )
    ::-------------------------- obtain a state and its population size
	set pop=%1
	echo state: %state% pop: %pop%
	echo path: %datadir%/%state%
	mkdir %datadir%\%state%
	set srcdir="%datadir%\%state%\fhir"
	set destdir="%datadir%%suffix%\%state%"
	mkdir %destDir%
    ::--------------------------
	call "%SYNTHEA_HOME%\bin\run_synthea.bat" -p %pop% --exporter.baseDirectory "%datadir%\%state%" %state% 
    %JAVA_HOME%\bin\java -classpath "..\lib\loader\db;..\lib\load\*;..\lib\*" com.frt.fhir.load.FhirBundleExtract %srcdir% %destdir%
    shift
	set "srcdir="
	set "destdir="
	set "state="
	set "pop="
    goto loop

REM source ../env.sh
REM  $1 data-base-dir $2 interval $3 $4 $5 $6
REM echo "parameter count:" $#

REM if (( $# < 3 )); then
REM   echo "usage: fhir-stream-feeder.sh <base.dir> <interval> [<state-1> <population-1> ...<state-k> <population-k>]"
REM   echo "example: fhir-stream-feeder.sh ./data 5000 California 1000 Arizona 600 Washington 980"
REM   exit 0
REM else
REM   datadir="$1"; shift
REM   interval="$1"; shift  
REM   echo "datadir:" $datadir "interval:" $interval
REM fi

REM while [ -n "$1" ]
REM do
REM     state="$1"; shift
REM     if [ -n "$1" ]; then
REM         pop="$1"; shift
REM     else
REM 	echo "malformed arguments, each <state> requires <population>..."   
REM         echo "usage: fhir-stream-feeder.sh <base.dir> <interval> [<state-1> <population-1> ...<state-k> <population-k>]"
REM         echo "example: fhir-stream-feeder.sh ./data 5000 California 1000 Arizona 600 Washington 980"
REM         exit 0
REM     fi
REM     echo "state:" $state "pop:" $pop
REM     echo "path:" "$datadir/$state"
REM     mkdir -p $datadir/$state
REM     srcdir="$datadir/$state/fhir"
REM     destdir="$datadir$suffix/$state"
REM     mkdir -p $destDir
REM     $SYNTHEA_HOME/bin/run_synthea.sh -p $pop --exporter.baseDirectory "$datadir/$state" $state 
REM     $JAVA_HOME/bin/java -classpath "../lib/loader/db:../lib/load/*:../lib/*" com.frt.fhir.load.FhirBundleExtract $srcdir $destdir
REM     unset srcdir;
REM     unset destdir;
REM     unset state;
REM     unset pop;
REM done

REM feeder_base="$datadir$suffix"
REM $JAVA_HOME/bin/java -classpath "../lib/*" com.frt.stream.io.FhirStreamWriter $feeder_base $interval

:usage
   echo "usage: fhir-stream-feeder.bat <base.dir> <interval> [<state-1> <population-1> ...<state-k> <population-k>]"
   echo "example: fhir-stream-feeder.bat .\data 5000 California 1000 Arizona 600 Washington 980"

:end

pause
