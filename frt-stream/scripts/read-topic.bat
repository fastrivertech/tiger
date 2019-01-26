REM read topics
%KAFKA_HOME%\bin\windows\kafka-run-class kafka.tools.GetOffsetShell --broker-list localhost:9092 --topic %1
