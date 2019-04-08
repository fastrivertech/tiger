export CLASSPATH="/home/ec2-user/confluent-5.2.0/share/java/kafka-connect-jdbc/*"
export CONFLUENT_HOME=$HOME/confluent-5.2.0
./connect-standalone $CONFLUENT_HOME/etc/schema-registry/fhir-connect-avro-standalone.properties $CONFLUENT_HOME/etc/kafka/fhir-org-file-source.properties CONFLUENT_HOME/etc/kafka-connect-jdbc/fhir-gp-mysql-sink.properties