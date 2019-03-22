#!/bin/bash

source ../setEnv.sh

## start a single zookeeper server
echo "exec zk==================="
$KAFKA_HOME/bin/zookeeper-server-start.sh $KAFKA_HOME/config/zookeeper.properties


