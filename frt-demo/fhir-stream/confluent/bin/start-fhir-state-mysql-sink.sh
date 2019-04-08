export CLASSPATH="/home/ec2-user/confluent-5.2.1/share/java/kafka-connect-jdbc/*"
export CONFLUENT_HOME=$HOME/confluent-5.2.1
./connect-standalone $CONFLUENT_HOME/etc/schema-registry/fhir-connect-avro-standalone.properties $CONFLUENT_HOME/etc/kafka-connect-jdbc/fhir-groupby-state-mysql-sink.properties
