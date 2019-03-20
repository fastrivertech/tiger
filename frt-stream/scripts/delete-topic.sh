#!/bin/bash
## delete a topic
## delete-topics.sh topic host
HOST=$2
if ["$HOST" = ""]; then
	HOST="localhost"
fi 
$KAFKA_HOME/bin/kafka-topics.sh --delete --zookeeper $HOST:2181 --topic $1
