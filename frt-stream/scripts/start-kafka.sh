#!/bin/bash

source ../setEnv.sh

## start a single kafka server
echo "exec kafka==================="
$KAFKA_HOME/bin/kafka-server-start.sh $KAFKA_HOME/config/server.properties


