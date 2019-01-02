REM create a topic
%KAFKA_HOME%\bin\windows\kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic FhirTopic
