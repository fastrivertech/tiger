#!/bin/bash
## delete a topic
## delete-topics.sh topic host
HOST=$2
if ["$HOST" = ""]; then
	HOST="localhost"
fi 

source ../env.sh

$KAFKA_HOME/bin/kafka-topics.sh --delete --zookeeper $HOST:12181 --topic $1
