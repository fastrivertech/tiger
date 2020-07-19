#!/bin/bash

source ../env.sh

## start a single zookeeper server
echo "exec zk==================="
$KAFKA_HOME/bin/zookeeper-server-start.sh $KAFKA_HOME/config/zookeeper.properties &

## start a single kafka server
echo "exec kafka==================="
$KAFKA_HOME/bin/kafka-server-start.sh $KAFKA_HOME/config/server.properties &


