#!/bin/bash
./kafka-topics.sh --create --zookeeper localhost:12181 --replication-factor 1 --partitions 1 --topic TestTopic
