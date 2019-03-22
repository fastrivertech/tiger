#!/bin/bash

source ../setEnv.sh

## stop a single zookeeper server
$KAFKA_HOME/bin/zookeeper-server-stop.sh
