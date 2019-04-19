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

set suffix="extracted"
set "SYNTHEA_HOME=../synthea"

echo base.dir: %datadir% interval: %interval%
echo SYNTHEA HOME %SYNTHEA_HOME% SUFFIX %suffix%

if not exist %datadir% mkdir %datadir%

:loop
    ::-------------------------- check list of state and population size
    if ""%1""=="""" (
		echo Done with synthetic patient json generation and patient resource extraction.
        goto feeder
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
	set srcdir="%datadir%\%state%\fhir_r4"
	set destdir="%datadir%%suffix%\%state%"
	mkdir %destDir%
    ::--------------------------
	call "%SYNTHEA_HOME%\bin\run_synthea.bat" -p %pop% %state% --exporter.fhir_r4.export true --exporter.fhir.export false --exporter.baseDirectory "%datadir%\%state%"
    %JAVA_HOME%\bin\java -classpath "..\lib\loader\db;..\lib\load\*;..\lib\*" com.frt.fhir.load.FhirBundleExtract %srcdir% %destdir%
    shift
	set "srcdir="
	set "destdir="
	set "state="
	set "pop="
    goto loop

:feeder

set feeder_base="%datadir%%suffix%"
%JAVA_HOME%\bin\java -classpath "..\lib\*" com.frt.stream.io.FhirStreamWriter %feeder_base% %interval%
goto end

:usage
   echo "usage: fhir-stream-feeder.bat <base.dir> <interval> [<state-1> <population-1> ...<state-k> <population-k>]"
   echo "example: fhir-stream-feeder.bat .\data 5000 California 1000 Arizona 600 Washington 980"

:end

