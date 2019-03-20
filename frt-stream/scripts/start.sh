#!/bin/bash
## start a single zookeeper server
start $KAFKA_HOME/bin/zookeeper-server-start.sh $KAFKA_HOME/config/zookeeper.properties
## start a single kafka server
start $KAFKA_HOME/bin/kafka-server-start.sh $KAFKA_HOME/config/server.properties


