REM list topics
REM list-topics.bat host
set HOST=%1
if "%HOST%" == "" (
	set HOST=localhost
) 
%KAFKA_HOME%\bin\windows\kafka-topics --list --zookeeper %HOST%:2181
