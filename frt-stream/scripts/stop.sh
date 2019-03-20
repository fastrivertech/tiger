#!/bin/bash
## stop a single kafka server
start $KAFKA_HOME/bin/kafka-server-stop.sh
## stop a single zookeeper server
start $KAFKA_HOME/bin/zookeeper-server-stop.sh
