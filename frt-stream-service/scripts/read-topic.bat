REM read topics
REM read-topic.bat topic host
set HOST=%2
if "%HOST%" == "" (
	set HOST=localhost
) 
%KAFKA_HOME%\bin\windows\kafka-run-class kafka.tools.GetOffsetShell --broker-list %HOST%:9092 --topic %1
