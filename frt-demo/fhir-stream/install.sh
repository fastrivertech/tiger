#!/bin/bash
## Copyright(c) 2018 Fast River Technologies Inc. All Rights Reserved. 
## @echo off

if (( $# < 1 )); then
  echo "usage: deploy2confluent.sh <confluent-base-dir>"
  echo "example: deploy2confluent.sh ~/confluent-5.2.0"
  exit 0
else
  echo "parameter count:" $#
  export CONFLUENT_BASE=$1
  if [ ! -d $CONFLUENT_BASE ]; then
	echo confluent base directory $CONFLUENT_BASE not found!
	exit 0
  fi
fi

echo Copying kafka scripts and properties to confluent at: $CONFLUENT_BASE
cp ./confluent/etc/kafka-connect-jdbc/*.* $CONFLUENT_BASE/etc/kafka-connect-jdbc
echo Copied kafka-connect-jdbc properties to : $CONFLUENT_BASE/etc/kafka-connect-jdbc
cp ./confluent/etc/schema-registry/*.* $CONFLUENT_BASE/etc/schema-registry
echo Copied schema-registry properties to : $CONFLUENT_BASE/etc/schema-registry
cp ./confluent/share/java/kafka-connect-jdbc/*.jar $CONFLUENT_BASE/share/java/kafka-connect-jdbc
echo Copied mysql jdbc driver to: $CONFLUENT_BASE/share/java/kafka-connect-jdbc
cp ./confluent/bin/*.* $CONFLUENT_BASE/bin
echo Copied KSQL stream scripts, KAFKA topic scripts, and Confluent connector scripts to: $CONFLUENT_BASE/bin
cp ./data/*.csv $CONFLUENT_BASE/bin
echo Copied CSV data to where the scripts located: $CONFLUENT_BASE/bin

chmod ugo+x $CONFLUENT_BASE/bin/*.sh

echo FHIR Stream Analytical Tools Deployed.
