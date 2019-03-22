#!/bin/bash
## list topics
## list-topics.sh host
HOST=$1
if ["$HOST" = ""]; then
	HOST="localhost"
fi

source ../setEnv.sh

$KAFKA_HOME/bin/kafka-topics.sh --list --zookeeper $HOST:12181
