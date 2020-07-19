#!/bin/bash

source ../env.sh

## stop a single kafka server
$KAFKA_HOME/bin/kafka-server-stop.sh ;

## stop a single zookeeper server
$KAFKA_HOME/bin/zookeeper-server-stop.sh
