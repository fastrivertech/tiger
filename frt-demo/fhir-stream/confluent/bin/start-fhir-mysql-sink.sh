export CLASSPATH="/home/ec2-user/confluent-5.2.0/share/java/kafka-connect-jdbc/*"
./connect-standalone $HOME/confluent-5.2.0/etc/schema-registry/fhir-connect-avro-standalone.properties $HOME/confluent-5.2.0/etc/kafka-connect-jdbc/fhir-groupby-org-mysql-sink.properties
