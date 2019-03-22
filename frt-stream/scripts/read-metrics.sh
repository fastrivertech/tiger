#!/bin/bash
## read topic metrics
## read-metrics.sh topic host
HOST=$2
if ["$HOST" = ""]; then
	HOST="localhost"
fi

source ../setEnv.sh

$KAFKA_HOME/bin/kafka-console-consumer.sh --bootstrap-server $HOST:19092 --topic $1 --formatter kafka.tools.DefaultMessageFormatter --property print.key=true --property print.value=true --property key.deserializer=org.apache.kafka.common.serialization.StringDeserializer --property value.deserializer=org.apache.kafka.common.serialization.LongDeserializer
