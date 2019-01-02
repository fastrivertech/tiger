REM start a single zookeeper server
start %KAFKA_HOME%\bin\windows\zookeeper-server-start C:/apache/kafka/kafka_2.12-2.1.0/config/zookeeper.properties
REM start a single kafka server
start %KAFKA_HOME%\bin\windows\kafka-server-start C:/apache/kafka/kafka_2.12-2.1.0/config/server.properties


