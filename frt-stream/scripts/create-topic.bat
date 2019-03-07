REM create a topic
REM create-topics.bat topic host
set HOST=%2
if "%HOST%" == "" (
	set HOST=localhost
) 
%KAFKA_HOME%\bin\windows\kafka-topics --create --zookeeper %HOST%:2181 --replication-factor 1 --partitions 1 --topic %1
