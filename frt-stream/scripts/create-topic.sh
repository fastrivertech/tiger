#!/bin/bash
## create a topic
## create-topics.sh topic host
HOST=$2
if ["$HOST" = ""]; then
	HOST="localhost"
fi 
$KAFKA_HOME/bin/kafka-topics.sh --create --zookeeper $HOST:2181 --replication-factor 1 --partitions 1 --topic $1
