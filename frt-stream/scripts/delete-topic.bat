REM delete a topic
REM delete-topics.bat topic host
set HOST=%2
if "%HOST%" == "" (
	set HOST=localhost
) 
%KAFKA_HOME%\bin\windows\kafka-topics --delete --zookeeper %HOST%:2181 --topic %1
