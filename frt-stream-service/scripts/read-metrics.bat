REM read topic metrics
REM read-metrics.bat topic host
set HOST=%2
if "%HOST%" == "" (
	set HOST=localhost
)
%KAFKA_HOME%\bin\windows\kafka-console-consumer.bat --bootstrap-server %HOST%:9092 --topic %1 --formatter kafka.tools.DefaultMessageFormatter --property print.key=true --property print.value=true --property key.deserializer=org.apache.kafka.common.serialization.StringDeserializer --property value.deserializer=org.apache.kafka.common.serialization.LongDeserializer
