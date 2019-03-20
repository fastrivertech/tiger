#!/bin/bash
## read topics
## read-topic.bat topic host
HOST=$2
if ["$HOST" = ""]; then
	HOST="localhost"
fi
$KAFKA_HOME/bin/kafka-run-class.sh kafka.tools.GetOffsetShell --broker-list $HOST:9092 --topic $1
